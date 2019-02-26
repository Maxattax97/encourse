import React, { Component } from 'react'
import { LineChart, XAxis, YAxis,
    Tooltip, Legend, Line, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'
import {retrieveStudentProgress} from '../../../../redux/retrievals/student'
import {getCurrentStudent, getStudentCharts, getStudentProgress} from '../../../../redux/state-peekers/student'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'
import {Chart} from '../../../Helpers'

class StudentProgress extends Component {

    dateFormatter = (dateUnix) => {
        return moment(dateUnix).format('M-D')
    }

    /*render() {
        return (
            <Chart
                chart={this.props.chart}
                title='Percentage of passed test cases over time. The score is calculated by looking at the latest commit that day that compiles, so the score can artificially fall if the student leaves the project on a broken commit.'
            >
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart className="chart" width={730} height={500} data={this.props.chart.data}
                               margin={{ top: 20, right: 35, left: 20, bottom: 20 }} syncId="date">
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Student Progress Over Time</text>
                        <XAxis dataKey="date" type="number" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                            <Label value="Date" position="bottom" />
                        </XAxis>
                        <YAxis domain={[0, 100]}>
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                % Completion
                            </Label>
                        </YAxis>
                        <Tooltip labelFormatter={this.dateFormatter}/>
                        <Legend verticalAlign="top"/>
                        <Line type="monotone" dataKey="progress" stroke="#0057A7CC" />
                    </LineChart>
                </ResponsiveContainer>
            </Chart>
        )
    }*/

    formatData = (data) => {
        if(!data)
            return data

        let previousProgress = 0;

        return data.map(commit => {
            if(commit.visiblePoints || commit.hiddenPoints)
                previousProgress = commit.visiblePoints + commit.hiddenPoints;
            return {
                date: commit.date,
                progress: previousProgress
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
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Progress Made on Project</text>
                        <XAxis dataKey="date" tickFormatter={tick => moment(tick).format('M-D')}>
                            <Label position="insideBottom" offset={-15} value="Date"/>
                        </XAxis>
                        <YAxis type="number">
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Points Earned
                            </Label>
                        </YAxis>
                        <Tooltip labelFormatter={tick => moment(tick).format('M-D HH:mm:ss')}/>
                        <Line name="progress" type="monotone" dataKey="progress" stroke="black" dot={false} isAnimationActive={false}/>
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

export default connect(mapStateToProps)(StudentProgress)
