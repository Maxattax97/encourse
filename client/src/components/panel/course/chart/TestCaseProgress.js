import React, { Component } from 'react'
import { ComposedChart, Bar, Brush, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'
import {
	retrieveCourseTestProgress,
	retrieveStudentsTestProgress
} from "../../../../redux/retrievals/course"
import {getCurrentProject} from "../../../../redux/state-peekers/project"
import {
	getCourseTestProgress,
	getStudentsTestProgress
} from "../../../../redux/state-peekers/course"
import {Chart} from "../../../Helpers"

const toPercent = (decimal, fixed = 0) => {
	return `${(decimal * 100).toFixed(fixed)}%`
}

class StudentsTestCaseProgress extends Component {

	componentWillMount() {
		if(this.props.project){
			if(this.props.anon)
				retrieveCourseTestProgress(this.props.project)
			else
				retrieveStudentsTestProgress(this.props.project)
		}
	}

	componentWillReceiveProps = (nextProps) => {
		if(nextProps.project && (!(this.props.project) || this.props.project.index !== nextProps.project.index)) {
			if(this.props.anon)
				retrieveCourseTestProgress(nextProps.project)
			else
				retrieveStudentsTestProgress(nextProps.project)
		}
	}

	render() {
		return (
			<Chart chart={this.props.chart}>
				<ResponsiveContainer width="100%" height="100%">
					<ComposedChart
						data={this.props.chart.data}
						margin={{top: 5, right: 30, left: 30, bottom: 35}}
					>
						<CartesianGrid/>
						<XAxis dataKey="testName" type="category">
							<Label offset={-10} position="insideBottom">
								{/*Test Case*/}
							</Label>
						</XAxis>
						<YAxis tickFormatter={toPercent} domain={[0, 1]}>
							<Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
								% of Class
							</Label>
						</YAxis>
						<Tooltip/>
						<Bar dataKey="percent">
							{
								this.props.chart.data.map((entry, index) =>
									<Cell key={Date.now()+index} fill={entry.hidden ? '#005599' : '#8884d8' }/>
								)
							}
						</Bar>
						<Brush dataKey="testName" height={40} stroke="#8884d8"/>
					</ComposedChart>
				</ResponsiveContainer>
			</Chart>
		)
	}
}

const mapStateToProps = (state, props) => {
	return {
		project: getCurrentProject(state),
		chart: props.anon ? getCourseTestProgress(state) : getStudentsTestProgress(state)
	}
}

export default connect(mapStateToProps)(StudentsTestCaseProgress)
