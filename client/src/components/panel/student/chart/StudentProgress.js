import React, { Component } from 'react'
import { LineChart, CartesianGrid, XAxis, YAxis, Brush,
	Tooltip, Legend, Line, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'
import {retrieveStudentProgress} from "../../../../redux/retrievals/student"
import {getCurrentStudent, getStudentProgress} from "../../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../../redux/state-peekers/project"
import {Chart} from "../../../Helpers"

class StudentProgress extends Component {

	componentDidMount() {
		if(this.props.student && this.props.project)
			retrieveStudentProgress(this.props.student, this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveStudentProgress(this.props.project)
	}

	dateFormatter = (dateUnix) => {
		return moment(dateUnix).format('M-D')
	}

	render() {
		return (
			<Chart chart={this.props.chart}>
				<ResponsiveContainer width="100%" height="100%">
					<LineChart className="chart" width={730} height={500} data={this.props.chart.data}
					           margin={{ top: 20, right: 35, left: 20, bottom: 20 }}>
						<text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Student Progress Over Time</text>
						<CartesianGrid strokeDasharray="3 3" />
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
						<Line type="monotone" dataKey="progress" stroke="#8884d8" />
						<Brush dataKey="date" height={20} stroke="#8884d8" tickFormatter={this.dateFormatter}/>
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
		chart: getStudentProgress(state)
	}
}

export default connect(mapStateToProps)(StudentProgress)
