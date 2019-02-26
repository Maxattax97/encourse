import React, { Component } from 'react'
import {
    AreaChart,
    Area,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Label,
    ResponsiveContainer,
    Line,
    LineChart
} from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import {Chart} from '../../../Helpers'
import {retrieveStudentCodeChanges} from '../../../../redux/retrievals/student'
import {
    getCurrentStudent, getStudentCharts,
    getStudentCodeChanges
} from '../../../../redux/state-peekers/student'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'
import {hourMinutesFromMinutes} from '../../../../common/time-helpers'

class MinutesWorkedLine extends Component {

    formatData = (data) => {
        if(!data)
            return data

        let startDate = data[0].date;
        let endDate = data[data.length - 1].date;

        return data.map(commit => {
            return {
                date: commit.date,
                time: (commit.seconds / 60),
                timeExpected: commit.date === startDate ? (commit.seconds / 60) : commit.date === endDate ? (commit.seconds / 60) : null
            }
        })
    }

    render() {
        return (
            <Chart
                chart={this.props.charts}
            >
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={this.formatData(this.props.charts.data.commits)} margin={{top: 40, right: 35, left: 0, bottom: 25}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Amount of Time Spent on Project</text>
                        <XAxis dataKey="date" tickFormatter={tick => moment(tick).format('M-D')}>
                            <Label position="insideBottom" offset={-15} value="Date"/>
                        </XAxis>
                        <YAxis type="number">
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Minutes Worked
                            </Label>
                        </YAxis>
                        <Tooltip labelFormatter={tick => moment(tick).format('M-D HH:mm:ss')} formatter={(value, name, props) => hourMinutesFromMinutes(value * 60) }/>
                        <Line name="time" type="monotone" dataKey="time" stroke="black" dot={false} isAnimationActive={false}/>
                        <Line connectNulls={true} type='monotone' dataKey='timeExpected' stroke='red' strokeDasharray="5 5" dot={false} isAnimationActive={false} />
                    </LineChart>
                </ResponsiveContainer>
            </Chart>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        charts: getStudentCharts(state)
    }
}

export default connect(mapStateToProps)(MinutesWorkedLine)
