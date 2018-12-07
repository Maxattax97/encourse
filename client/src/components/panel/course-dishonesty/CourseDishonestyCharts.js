import React, { Component } from 'react'
import {Card, ChartList, Summary} from '../../Helpers'
import CourseIdenticalLines from "./chart/StudentsSimilarity"
import StudentsSimilarityTable from "./chart/StudentsSimilarityTable"

class CourseDishonestyCharts extends Component {

	render() {
		return (
			<ChartList>
				<CourseIdenticalLines/>
				<StudentsSimilarityTable/>
			</ChartList>
		)
	}

}

export default CourseDishonestyCharts