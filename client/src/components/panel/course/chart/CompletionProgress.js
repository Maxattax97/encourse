import React, { Component } from 'react'
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import CustomTooltipContent from './CustomTooltipContent';
import {getCurrentProject} from "../../../../redux/state-peekers/project"
import {getCourseProgress, getStudentsProgress} from "../../../../redux/state-peekers/course"
import {retrieveCourseProgress, retrieveStudentsProgress} from "../../../../redux/retrievals/course"
import {Chart} from "../../../Helpers"

class CompletionProgress extends Component {

	componentWillMount() {
		if (this.props.project) {
			if (this.props.anon)
				retrieveCourseProgress(this.props.project)
			else
				retrieveStudentsProgress(this.props.project)
		}
	}

	componentDidUpdate = (prevProps) => {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index)) {
			if(prevProps.anon)
				retrieveCourseProgress(this.props.project)
			else
				retrieveStudentsProgress(this.props.project)
		}
	}

	render() {
		return (
			<Chart chart={this.props.chart}>
				<ResponsiveContainer width="100%" height="100%">
					<ComposedChart
						data={this.props.chart.data}
						margin={{top: 5, right: 30, left: 30, bottom: 35}}
						barCategoryGap={0}
					>
						<CartesianGrid/>
						<XAxis dataKey="progressBin" type="category">
							<Label offset={-10} position="insideBottom">
								Progress
							</Label>
						</XAxis>
						<YAxis>
							<Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
								Students
							</Label>
						</YAxis>
						<Tooltip content={<CustomTooltipContent />} />
						<Bar dataKey="count" fill="#8884d8"/>
					</ComposedChart>
				</ResponsiveContainer>
			</Chart>
		)
	}
}

const mapStateToProps = (state, props) => {
	return {
		project: getCurrentProject(state),
		chart: props.anon ? getCourseProgress(state) : getStudentsProgress(state)
	}
}

export default connect(mapStateToProps)(CompletionProgress)
