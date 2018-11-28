
import React, { Component } from 'react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Label, Brush, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import {Chart} from '../../../Helpers'
import {retrieveStudentCommitFrequency} from "../../../../redux/retrievals/student"
import {getCurrentStudent, getStudentCommitFrequency} from "../../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../../redux/state-peekers/project"

class CommitHistoryHistogram extends Component {

	componentWillMount() {
		if(this.props.student && this.props.project)
			retrieveStudentCommitFrequency(this.props.student, this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveStudentCommitFrequency(this.props.project)
	}

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }

    render() {
        return (
            <Chart chart={this.props.chart}>
	            <ResponsiveContainer width="100%" height="100%">
		            <BarChart data={this.props.chart.data} margin={{top: 40, right: 35, left: 20, bottom: 30}}>
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
            </Chart>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        chart: getStudentCommitFrequency(state)
    }
}

export default connect(mapStateToProps)(CommitHistoryHistogram)
