import React, { Component } from 'react'
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import CustomTooltipContent from './CustomTooltipContent';
import {getCurrentProject} from "../../../../redux/state-peekers/projects"
import {getCourseProgress, getStudentsProgress} from "../../../../redux/state-peekers/course"
import {retrieveCourseProgress, retrieveStudentsProgress, retrieveStudentsProgressSpecific} from "../../../../redux/retrievals/course"
import {getSelected, isAnySelected} from '../../../../redux/state-peekers/control'
import {Chart} from "../../../Helpers"

class CompletionProgress extends Component {

	componentDidMount() {
		if (this.props.project) {
			if (this.props.anon)
				retrieveCourseProgress(this.props.project)
			else {
				if(this.props.isAnySelected && this.props.selected && this.props.isAnySelected >= 2) {
					let a = Object.keys(this.props.selected.explict)
					retrieveStudentsProgressSpecific(this.props.project, a)
				} else {
					retrieveStudentsProgress(this.props.project)
				}
			}	
		}
	}

	componentDidUpdate = (prevProps) => {
		if (prevProps.anon) {
			if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index)) {
				retrieveCourseProgress(this.props.project)
			}
		}
		else {
			if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index) || prevProps.isAnySelected !== this.props.isAnySelected) {
				if(this.props.isAnySelected && this.props.isAnySelected >= 2 && !this.props.selected.selectedAll) {
					let a = Object.keys(this.props.selected.explict)
					retrieveStudentsProgressSpecific(this.props.project, a)
				} else {
					retrieveStudentsProgress(this.props.project)
				}
			}
		}
	}

	render() {
		return (
			<Chart
                chart={this.props.chart}
                title='Histogram of all students in the course grouped by their current visible test scores. Scores are calculated by summing the points given for each passing test case visible to the student.'
            >
				<ResponsiveContainer
                    width="100%"
                    height="100%"
                >
					<ComposedChart
						data={this.props.chart.data}
						margin={{top: 5, right: 10, left: 5, bottom: 15}}
						barCategoryGap={0}
					>
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
						<Tooltip content={<CustomTooltipContent />} animationDuration={100}/>
						<Bar dataKey="count" fill="#0057A7CC" isAnimationActive={false}/>
					</ComposedChart>
				</ResponsiveContainer>
			</Chart>
		)
	}
}

const mapStateToProps = (state, props) => {
	return {
		project: getCurrentProject(state),
		selected: getSelected(state, 'students'),
		chart: props.anon ? getCourseProgress(state) : getStudentsProgress(state),
		isAnySelected: isAnySelected(state, 'students')
	}
}

export default connect(mapStateToProps)(CompletionProgress)
