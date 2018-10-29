import React, { Component } from 'react'
import { LineChart, CartesianGrid, XAxis, YAxis, Brush,
    Tooltip, Legend, Line, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'
import { fuzzing } from '../../../../fuzz'

import { getProgressLine } from '../../../../redux/actions/index'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const defaultData = [
    {date: moment('9/10/18').valueOf(), progress: 0},
    {date: moment('9/11/18').valueOf(), progress: 0},
    {date: moment('9/12/18').valueOf(), progress: 0},
    {date: moment('9/13/18').valueOf(), progress: 0},
    {date: moment('9/14/18').valueOf(), progress: 0},
    {date: moment('9/15/18').valueOf(), progress: 0},
    {date: moment('9/16/18').valueOf(), progress: 0},
]

class StudentProgressPerDay extends Component {
    constructor(props) {
        super(props)

        this.state = {
            formattedData: defaultData,
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps(nextProps) {
        if(this.props.isLoading && !nextProps.isLoading) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        if(props.currentProjectId) {
            props.getData(`${url}/api/progress?projectID=${props.currentProjectId}&userName=${props.currentStudent.id}`)
        }      
    }


    formatApiData = (udata) => {
        if (!udata || !udata.data) {
            return defaultData
        }
        const data = udata.data
        for (let entry of data) {
            entry.date = moment(entry.date).valueOf()
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
                    progress: inputEntry.progress,
                })
            }
        }

        if (fuzzing) {
            let start = parseInt(Math.random() * formattedData.length)
            let min = 0
            for (start; start < formattedData.length; start++) {
                const entry = formattedData[start]

                min = Math.max(min, entry.progress)
                entry.progress = parseInt(Math.random() * (100 - min) + min)
                min = Math.min(Math.max(min, entry.progress + 20), 100)
            }
        }

        return formattedData
    }

    dateFormatter = (dateUnix) => {
        return moment(dateUnix).format('M-D')
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart className="chart" width={730} height={500} data={this.state.formattedData}
                            margin={{ top: 20, right: 35, left: 20, bottom: 20 }}>
                            <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Student Progress Over Time (Incomplete)</text>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="date" type="number" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                                <Label value="Date" position="bottom" />
                            </XAxis>
                            <YAxis>
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                            % Completion
                                </Label>
                            </YAxis>
                            <Tooltip labelFormatter={this.dateFormatter}/>
                            <Legend verticalAlign="top"/>
                            <Line type="monotone" dataKey="progress" stroke="#8884d8" />
                            <Brush dataKey="date" height={20} stroke="#8884d8" tickFormatter={this.dateFormatter}/>
                        </LineChart>
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
        data: state.student && state.student.getProgressLineData ? state.student.getProgressLineData : null,
        isLoading: state.student ? state.student.getProgressLineIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getProgressLine(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentProgressPerDay)
