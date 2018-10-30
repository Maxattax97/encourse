
import React, { Component } from 'react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Label, Brush, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import { getCommitFrequency } from '../../../../redux/actions/index'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const defaultData = [
    {date: moment('2018-09-16').valueOf(), count: 0},
    {date: moment('2018-09-17').valueOf(), count: 0},
    {date: moment('2018-09-18').valueOf(), count: 0},
    {date: moment('2018-09-19').valueOf(), count: 0},
    {date: moment('2018-09-20').valueOf(), count: 0},
    {date: moment('2018-09-21').valueOf(), count: 0},
    {date: moment('2018-09-22').valueOf(), count: 0},
    {date: moment('2018-09-23').valueOf(), count: 0},
    {date: moment('2018-09-24').valueOf(), count: 0},
    {date: moment('2018-09-25').valueOf(), count: 0},
    {date: moment('2018-09-26').valueOf(), count: 0},
    {date: moment('2018-09-27').valueOf(), count: 0},
]

class CommitHistoryHistogram extends Component {

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
            props.getData(`${url}/api/commitCount?projectID=${props.currentProjectId}&userName=${props.currentStudent.id}`)
        }
    }

    dateFormatter = (date) => {
        return moment(date).format('M-D')
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
        }

        return data
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <BarChart data={this.state.formattedData} margin={{top: 40, right: 35, left: 20, bottom: 30}}>
                            <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Commit Frequency</text>
                            <CartesianGrid/>
                            <XAxis dataKey="date" tickFormatter={this.dateFormatter}>
                                <Label offset={-15} position="insideBottom">
                                Commits
                                </Label>
                            </XAxis>
                            <YAxis>
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Date
                                </Label>
                            </YAxis>
                            <Tooltip labelFormatter={this.dateFormatter} animationDuration={500}/>
                            <Bar dataKey="count" fill="#8884d8"/>
                            <Brush dataKey="date" height={20} stroke="#8884d8" tickFormatter={this.dateFormatter} onChange={this.props.setBrush}/>
                        </BarChart>
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
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        data: state.student && state.student.getCommitFrequencyData ? state.student.getCommitFrequencyData : null,
        isLoading: state.student ? state.student.getCommitFrequencyIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getCommitFrequency(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CommitHistoryHistogram)