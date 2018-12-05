
import React, { Component } from 'react'
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import { getProgressPerTime } from '../../../../redux/actions/index'
import { getCurrentStudent } from '../../../../redux/state-peekers/student'
import { getCurrentProject } from "../../../../redux/state-peekers/projects"
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const defaultData = [
    {date: moment('2018-09-16').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-17').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-18').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-19').valueOf(), progress: 0, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-20').valueOf(), progress: 0, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-21').valueOf(), progress: 10, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-22').valueOf(), progress: 0, timeSpent: 5, commitCount: 120},
    {date: moment('2018-09-23').valueOf(), progress: 0, timeSpent: 1, commitCount: 30},
    {date: moment('2018-09-24').valueOf(), progress: 20, timeSpent: 2, commitCount: 100},
    {date: moment('2018-09-25').valueOf(), progress: 10, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-26').valueOf(), progress: 50, timeSpent: 7, commitCount: 200},
    {date: moment('2018-09-27').valueOf(), progress: 10, timeSpent: 3, commitCount: 50},
]

class StudentVelocityPerTime extends Component {

    constructor(props) {
        super(props)

        this.state = {
            formattedData: defaultData,
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentDidUpdate = (prevProps) => {
        if(prevProps.isLoading && !this.props.isLoading) {
            this.setState({ formattedData: this.formatApiData(this.props.data) })
        }
        if (this.props.project.id !== prevProps.project.id) {
            this.fetch(this.props)
        }
    }

    fetch = (props) => {
        if(props.project.id && props.student) {
            props.getData(`${url}/api/velocity?projectID=${props.project.id}&userName=${props.student.id}`)
        }  
    }

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }
    
    customDateFormatter = (value, name, props) => {
        if (name === 'date') {
            return moment(value).format('M-D')
        }
        return value
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }
        let data = udata.data

        if (!data || data.length === 0) {
            return defaultData
        }

        for (let entry of data) {
            entry.date = moment(entry.date).valueOf()
            entry.timeSpent = parseInt(entry.time_spent / 1000, 10)
            // progress per time spent
            entry.ppts = entry.timeSpent > 0 ? entry.progress / entry.timeSpent : 0
        }

        return data
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <ScatterChart data={this.state.formattedData} margin={{top: 40, right: 30, left: 20, bottom: 30}}>
                            <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Progress per Time Spent</text>
                            <CartesianGrid/>
                            <XAxis dataKey="date" type="number" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                                <Label offset={-15} position="insideBottom">
                                    Date
                                </Label>
                            </XAxis>
                            <YAxis dataKey="ppts" type="number">
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                    Progress per Time Spent
                                </Label>
                            </YAxis>
                            <Tooltip formatter={this.customDateFormatter}/>
                            <Scatter type="number" fill="#8884d8"/>
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
        student: getCurrentStudent(state),
		project: getCurrentProject(state),
        data: state.student && state.student.getProgressPerTimeData ? state.student.getProgressPerTimeData : null,
        isLoading: state.student ? state.student.getProgressPerTimeIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getProgressPerTime(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentVelocityPerTime)
