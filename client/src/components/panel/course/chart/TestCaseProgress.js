import React, { Component } from 'react'
import { ComposedChart, Bar, Brush, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'
import {
	retrieveCourseTestProgress,
	retrieveStudentsTestProgress,
	retrieveStudentsTestProgressSpecific
} from "../../../../redux/retrievals/course"
import {getCurrentProject} from "../../../../redux/state-peekers/projects"
import {
	getCourseTestProgress,
	getStudentsTestProgress
} from "../../../../redux/state-peekers/course"
import {Chart} from "../../../Helpers"
import {getSelected, isAnySelected} from '../../../../redux/state-peekers/control'

const toPercent = (decimal, fixed = 0) => {
	return `${(decimal * 100).toFixed(fixed)}%`
}

class StudentsTestCaseProgress extends Component {

	componentDidMount() {
		if(this.props.project){
			if(this.props.anon)
				retrieveCourseTestProgress(this.props.project)
			else {
				if(this.props.isAnySelected && this.props.isAnySelected >= 2) {
					let a = Object.keys(this.props.selected.explict)
					retrieveStudentsTestProgressSpecific(this.props.project, a)
				} else {
					retrieveStudentsTestProgress(this.props.project)
				}
			}
		}
	}

	componentDidUpdate = (prevProps) => {
		if (prevProps.anon) {
			if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index)) {
				retrieveCourseTestProgress(this.props.project)
			}
		}
		else {
			console.log(prevProps, this.props);
			if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index) || prevProps.isAnySelected !== this.props.isAnySelected) {
				console.log('update');
				if(this.props.isAnySelected && this.props.isAnySelected >= 2) {
					let a = Object.keys(this.props.selected.explict)
					retrieveStudentsTestProgressSpecific(this.props.project, a)
				} else {
					retrieveStudentsTestProgress(this.props.project)
				}
			}
		}
	}

	render() {
		return (
			<div>
				<Chart
					chart={this.props.chart}
					title='Bar graph of each test that indicates how many people passed each test.'
					style={{'padding': '40px'}}
				>
					<ResponsiveContainer width="100%" height="100%">
						<ComposedChart
							data={this.props.chart.data}
							margin={{top: 5, right: 15, left: 10, bottom: 15}}
						>
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
							<Tooltip animationDuration={100}/>
							<Bar dataKey="percent" isAnimationActive={false}>
								{
									this.props.chart.data.map((entry, index) =>
										<Cell key={Date.now()+index} fill={entry.hidden ? '#005599' : '#0057A7CC' }/>
									)
								}
							</Bar>
							<Brush dataKey="testName" height={40} stroke="#4caf50"/>
						</ComposedChart>
					</ResponsiveContainer>
				</Chart>
			</div>
		)
	}
}

const mapStateToProps = (state, props) => {
	return {
		project: getCurrentProject(state),
		selected: getSelected(state, 'students'),
		chart: props.anon ? getCourseTestProgress(state) : getStudentsTestProgress(state),
		isAnySelected: isAnySelected(state, 'students'),
	}
}

export default connect(mapStateToProps)(StudentsTestCaseProgress)
