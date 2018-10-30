import React, { Component } from 'react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Label, Brush, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import { getCodeFrequency } from '../../../../redux/actions/index'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const defaultData = [
    {date: moment('9/9/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/10/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/11/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/12/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/13/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/14/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/15/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/16/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/17/2018').valueOf(), additions: 0, deletions: -0},
    {date: moment('9/18/2018').valueOf(), additions: 0, deletions: -0},
]

class StudentCodeChanges extends Component {
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
            props.getData(`${url}/api/diffs?projectID=${props.currentProjectId}&userName=${props.currentStudent.id}`)
        } 
    }

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }

    formatApiData = (udata) => {
        if (!udata || !udata.data) {
            return defaultData
        }
        const data = udata.data

        for (let entry of data) {
            entry.date = moment(entry.date).valueOf()
            entry.deletions = -entry.deletions
        }

        if (!data || data.length === 0) {
            return defaultData
        }

        const minDate = data.reduce((min, p) => p.date < min ? p.date : min, data[0].date)
        const maxDate = data.reduce((max, p) => p.date > max ? p.date : max, data[0].date)
        
        const formattedData = []

        let inputIndex = 0
        for (let m = moment(minDate); m.diff(moment(maxDate), 'days') <= 0; m.add(1, 'days')) {
            const inputEntry = data[inputIndex]
            const inputDate = inputEntry.date

            if (m.isSame(inputDate, 'day')) {
                formattedData.push(inputEntry)
                inputIndex++
            }
            else {
                formattedData.push({
                    date: m.valueOf(),
                    additions: data[inputIndex - 1] || 0,
                    deletions: data[inputIndex - 1] || 0,
                })
            }
        }

        return formattedData
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <AreaChart data={this.state.formattedData} margin={{top: 40, right: 35, left: 0, bottom: 25}}>
                            <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Lines of Code Added/Deleted</text>
                            <CartesianGrid strokeDasharray="3 3"/>
                            <XAxis type="number" dataKey="date" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                                <Label position="insideBottom" offset={-15} value="Date"/>
                            </XAxis>
                            <YAxis/>
                            <Tooltip labelFormatter={this.dateFormatter}/>
                            <Area type="monotone" dataKey="additions" stroke="none" fill="green" />
                            <Area type="monotone" dataKey="deletions" stroke="none" fill="red" />
                            <Brush dataKey="date" height={20} stroke="#8884d8" tickFormatter={this.dateFormatter}/>
                        </AreaChart>
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
        data: state.student && state.student.getCodeFrequencyData ? state.student.getCodeFrequencyData : null,
        isLoading: state.student ? state.student.getCodeFrequencyIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getCodeFrequency(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentCodeChanges)
