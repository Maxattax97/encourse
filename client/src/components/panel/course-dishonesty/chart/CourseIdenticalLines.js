import React, { Component } from 'react' 
import { ScatterChart, Scatter, Dot, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import { getClassProgress } from '../../../../redux/actions/index'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'
// import CustomTooltipContent from './CustomTooltipContent';

const toPercent = (decimal, fixed = 2) => {
    return `${(decimal).toFixed(fixed)}`
}

const defaultData = [
    {
        'user1': 'user1',
        'user2': 'user2',
        'user1index': 1,
        'user2index': 2,
        'simularity': 10,
        'simularity-bin': 10,
        'height': 2
    },
    {
        'user1': 'user1',
        'user2': 'user3',
        'user1index': 1,
        'user2index': 3,
        'simularity-bin': 10,
        'simularity': 11,
        'height': 3
    },
    {
        'user1': 'user1',
        'user2': 'user4',
        'user1index': 1,
        'user2index': 4,
        'simularity-bin': 10,
        'simularity': 9,
        'height': 1
    },
    {
        'user1': 'user2',
        'user2': 'user3',
        'user1index': 2,
        'user2index': 3,
        'simularity-bin': 30,
        'simularity': 30,
        'height': 1
    },
    {
        'user1': 'user2',
        'user2': 'user4',
        'user1index': 2,
        'user2index': 4,
        'simularity-bin': 10,
        'simularity': 13,
        'height': 5
    },
    {
        'user1': 'user3',
        'user2': 'user4',
        'user1index': 3,
        'user2index': 4,
        'simularity-bin': 10,
        'simularity': 12,
        'height': 4
    },
];

class CourseIdenticalLinesChart extends Component {
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
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }
    
    fetch = (props) => {
        if(props.currentProjectId) {
            // TODO find endpoint
            // props.getData(`${url}/api/classProgress?projectID=${props.currentProjectId}`)
        }    
    }

    formatApiData = (udata) => {
        // TODO fix
        return defaultData
    }

    render() {
        console.log(this.state.formattedData);
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <ScatterChart
                            data={this.state.formattedData}
                            margin={{top: 5, right: 30, left: 30, bottom: 35}}
                            barCategoryGap={0}
                        >
                            <XAxis dataKey="simularity-bin" type="number">
                                <Label offset={-10} position="insideBottom">
                                    Student
                                </Label>
                            </XAxis>
                            <YAxis dataKey="height" type="number">
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                    Student
                                </Label>
                            </YAxis>
                            <Tooltip/>
                            <Scatter dataKey="count" fill="#8884d8"/>
                        </ScatterChart>
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
        // TODO map data to correct redux state
        // data: state.course && state.course.getClassProgressData ? state.course.getClassProgressData : null,
        // isLoading: state.course ? state.course.getClassProgressIsLoading : false,
        isLoading: false,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        // TODO map to correct dispatch
        getData: (url, headers, body) => dispatch(getClassProgress(url, headers, body))
    }
}

export { CourseIdenticalLinesChart }
export default connect(mapStateToProps, mapDispatchToProps)(CourseIdenticalLinesChart)
