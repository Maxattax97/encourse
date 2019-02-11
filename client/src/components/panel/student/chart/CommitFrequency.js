
import React, { Component } from 'react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import {Chart} from '../../../Helpers'
import {retrieveStudentCommitFrequency} from '../../../../redux/retrievals/student'
import {getCurrentStudent, getStudentCharts, getStudentCommitFrequency} from '../../../../redux/state-peekers/student'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'

class CommitHistoryHistogram extends Component {

    render() {
        return (
            <Chart
                chart={this.props.charts}
                title='Number of commits made by day.'
            >
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={this.props.charts.data.frequencies} margin={{top: 40, right: 35, left: 20, bottom: 30}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Commit Frequency</text>
                        <XAxis dataKey="date">
                            <Label offset={-15} position="insideBottom">
                                Commits
                            </Label>
                        </XAxis>
                        <YAxis domain={[0, 'dataMax']}>
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Date
                            </Label>
                        </YAxis>
                        <Tooltip animationDuration={500}/>
                        <Bar dataKey="frequency" fill="#0057A7CC"/>
                    </BarChart>
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

export default connect(mapStateToProps)(CommitHistoryHistogram)
