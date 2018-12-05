import React, { Component } from 'react' 
import { ScatterChart, Scatter, XAxis, YAxis, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'
import CustomTooltipContent from './CustomTooltipContent';

import {Chart, LoadingIcon} from '../../../Helpers'
import {
	retrieveStudentsSimilarity
} from "../../../../redux/retrievals/course"
import {getStudentsSimilarity} from "../../../../redux/state-peekers/course"
import {getCurrentProject} from "../../../../redux/state-peekers/projects"

class StudentsSimilarity extends Component {

	componentDidMount() {
		if (this.props.project)
			retrieveStudentsSimilarity(this.props.project)
	}

	componentDidUpdate = (prevProps) => {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
		    retrieveStudentsSimilarity(this.props.project)
	}

    render() {
        return (
	        <Chart
		        chart={this.props.chart}
		        title='Histogram of all students in the course grouped by the percentage of tests they are passing (progress).'
	        >
		        <ResponsiveContainer width="100%" height="100%">
			        <ScatterChart
				        data={this.props.chart.data}
				        margin={{top: 5, right: 30, left: 30, bottom: 35}}
				        barCategoryGap={0}
			        >
				        <XAxis dataKey="similarity_bin" type="number">
					        <Label offset={-10} position="insideBottom">
						        Identical lines of code
					        </Label>
				        </XAxis>
				        <YAxis dataKey="height" type="number">
					        <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
						        Count
					        </Label>
				        </YAxis>
				        <Tooltip content={<CustomTooltipContent />} />
				        <Scatter dataKey="height" fill="#8884d8"/>
			        </ScatterChart>
		        </ResponsiveContainer>
            </Chart>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        chart: getStudentsSimilarity(state),
        project: getCurrentProject(state)
    }
}

export default connect(mapStateToProps)(StudentsSimilarity)
