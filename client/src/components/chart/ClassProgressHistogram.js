import React, { Component } from 'react'
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import { getClassProgress } from '../../redux/actions'
import url from '../../server'
import {LoadingIcon} from '../Helpers'

const toPercent = (decimal, fixed = 0) => {
    return `${(decimal * 100).toFixed(fixed)}%`
}

const defaultData = [
    {
        'progressBin': '0-20%',
        'order': 0,
        'count': 10,
        'percent': 0.625
    },
    {
        'progressBin': '20-40%',
        'order': 20,
        'count': 3,
        'percent': 0.1875
    },
    {
        'progressBin': '40-60%',
        'order': 40,
        'count': 2,
        'percent': 0.125
    },
    {
        'progressBin': '60-80%',
        'order': 60,
        'count': 0,
        'percent': 0
    },
    {
        'progressBin': '80-100%',
        'order': 80,
        'count': 1,
        'percent': 0.0625
    }
]

class ClassProgressHistogram extends Component {
    constructor(props) {
        super(props)

        this.state = {
            formattedData: defaultData,
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if(this.props.isLoading && !nextProps.isLoading) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        if(props.projectID) {
            props.getData(`${url}/api/classProgress?projectID=${props.projectID}`)
        }    
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }

        const data = udata
        const formattedData = []
        const data2 = Object.entries(data)
        const total = data2.reduce((sum, p) => sum + p[1], 0)

        for (let apiEntry of data2) {
            const entry = {
                progressBin: apiEntry[0],
                order: parseInt(apiEntry[0]),
                count: apiEntry[1],
                percent: apiEntry[1] / total,
            }

            formattedData.push(entry)
        }

        formattedData.sort((a, b) => a.order - b.order)

        return formattedData
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <ComposedChart
                            data={this.state.formattedData}
                            margin={{top: 5, right: 30, left: 30, bottom: 35}}
                            barCategoryGap={0}
                        >
                            <CartesianGrid/>
                            <XAxis dataKey="progressBin" type="category">
                                <Label offset={-10} position="insideBottom">
                                % Completion
                                </Label>
                            </XAxis>
                            <YAxis tickFormatter={toPercent} domain={[0, 1]}>
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                % of Class
                                </Label>
                            </YAxis>
                            <Tooltip/>
                            <Bar dataKey="percent" fill="#8884d8"/>
                        </ComposedChart>
                    </ResponsiveContainer>
                </div>
                :
                <div className='chart-container loading'>
                    <LoadingIcon/>
                </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        data: state.course && state.course.getClassProgressData ? state.course.getClassProgressData : null,
        isLoading: state.course ? state.course.getClassProgressIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getClassProgress(url, headers, body))
    }
}

export { ClassProgressHistogram }
export default connect(mapStateToProps, mapDispatchToProps)(ClassProgressHistogram)
