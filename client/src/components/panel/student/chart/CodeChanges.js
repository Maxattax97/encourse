import React, { Component } from 'react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import {Chart} from '../../../Helpers'
import {retrieveStudentCodeChanges} from '../../../../redux/retrievals/student'
import {
    getCurrentStudent, getStudentCharts,
    getStudentCodeChanges
} from '../../../../redux/state-peekers/student'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'

class CodeChanges extends Component {

    formatData = (data) => {
        if(!data)
            return data

        let additions = 0
        let deletions = 0

        return data.map(commit => {
            additions += commit.additions
            deletions += commit.deletions

            return {
                date: commit.date,
                additions: additions,
                deletions: -deletions
            }
        })
    }

    render() {
        return (
            <Chart
                chart={this.props.charts}
                title='Cumulative number of lines added or deleted by day.'
            >
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={this.formatData(this.props.charts.data.commits)} margin={{top: 40, right: 35, left: 0, bottom: 25}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Lines of Code Added/Deleted</text>
                        <XAxis dataKey="date" domain={['dataMin', 'dataMax']} tickFormatter={tick => moment(tick).format('M-D')}>
                            <Label position="insideBottom" offset={-15} value="Date"/>
                        </XAxis>
                        <YAxis type="number">
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Changes
                            </Label>
                        </YAxis>
                        <Tooltip labelFormatter={tick => moment(tick).format('M-D HH:mm:ss')}/>
                        <Area name="deletions" type="monotone" dataKey="deletions" stroke="none" fill="red" />
                        <Area name="additions" type="monotone" dataKey="additions" stroke="none" fill="green" />
                    </AreaChart>
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

export default connect(mapStateToProps)(CodeChanges)
