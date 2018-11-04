import React, { Component } from 'react'
import { ComposedChart, Bar, Brush, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'
import { getTestBarGraph, getTestBarGraphAnon } from '../../../../redux/actions/index'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const toPercent = (decimal, fixed = 0) => {
    return `${(decimal * 100).toFixed(fixed)}%`
}

const defaultData1 = [
    {
        'testName': 'Test 1',
        'percent': .98,
        'hidden': false,
    },
    {
        'testName': 'Test 2',
        'percent': .94,
        'hidden': false,
    },
    {
        'testName': 'Test 3',
        'percent': 1,
        'hidden': false,
    },
    {
        'testName': 'Test 4',
        'percent': .89,
        'hidden': true,
    },
]
const defaultData2 = [
    {
        'testName': 'Test 1',
        'percent': .07,
        'hidden': false,
    },
    {
        'testName': 'Test 2',
        'percent': .08,
        'hidden': false,
    },
    {
        'testName': 'Test 3',
        'percent': .01,
        'hidden': false,
    },
    {
        'testName': 'Test 4',
        'percent': .01,
        'hidden': false,
    },
    {
        'testName': 'Test 5',
        'percent': .01,
        'hidden': false,
    },
    {
        'testName': 'Test 6',
        'percent': .02,
        'hidden': true,
    },
]

class StudentsTestCaseProgress extends Component {
    constructor(props) {
        super(props)

        this.state = {
            formattedData: this.getDefaultData()
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if(!this.props.anon) {
            if(this.props.isLoading && !nextProps.isLoading) {
                this.setState({ formattedData: this.formatApiData(nextProps.data) })
            }
        } else {
            if(this.props.isLoadingAnon && !nextProps.isLoadingAnon) {
                this.setState({ formattedData: this.formatApiData(nextProps.data) })
            }
        }   
        if (this.props.isLoading && !nextProps.isLoading) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }

    getDefaultData = (props) => {
        return (props && props.currentProjectId || this.props.currentProjectId) == 'cs252 Fall2018: MyMalloc' ? defaultData1 : defaultData2
    }

    fetch = (props) => {
        if(props.currentProjectId) {
            if(props.anon) {
                props.getAnonData(`${url}/api/testSummary?projectID=${props.currentProjectId}&anonymous=true`)
            } else {
                props.getData(`${url}/api/testSummary?projectID=${props.currentProjectId}`)
            }     
        }   
    }

    formatApiData = (udata) => {
        if(!udata || !udata.data || udata.data.length === 0) {
            return this.getDefaultData()
        }

        const data = udata.data
        const formattedData = []

        for (let apiEntry of data) {
            const entry = {
                testName: apiEntry.testName,
                hidden: apiEntry.hidden,
                percent: apiEntry.score / 100,
                score: apiEntry.score,
            }

            formattedData.push(entry)
        }

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
                        >
                            <CartesianGrid/>
                            <XAxis dataKey="testName" type="category">
                                <Label offset={-10} position="insideBottom">
                                {/*Test Case*/}
                                </Label>
                            </XAxis>
                            <YAxis tickFormatter={toPercent} domain={[0, 1]}>
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                % of Class
                                </Label>
                            </YAxis>
                            <Tooltip/>
                            <Bar dataKey="percent">
                                {this.state.formattedData.map((entry, index) => (
                                    <Cell key={Date.now()+index} fill={entry.hidden ? '#005599' : '#8884d8' }/>
                                ))}
                            </Bar>
                            <Brush dataKey="testName" height={40} stroke="#8884d8"/>
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
        data: state.course && state.course.getTestBarGraphData ? state.course.getTestBarGraphData : null,
        isLoading: state.course ? state.course.getTestBarGraphIsLoading : false,
        issLoadingAnon: state.course ? state.course.getTestBarGraphIsLoadingAnon : false,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getTestBarGraph(url, headers, body)),
        getAnonData: (url, headers, body) => dispatch(getTestBarGraphAnon(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentsTestCaseProgress)
