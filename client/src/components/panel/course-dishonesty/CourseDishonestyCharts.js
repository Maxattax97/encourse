import React, { Component } from 'react'
import {Card, ChartList, Summary} from '../../Helpers'
import CourseIdenticalLines from "./chart/StudentsSimilarity"

class CourseDishonestyCharts extends Component {

	render() {
		return (
			<ChartList>
				<CourseIdenticalLines/>
			</ChartList>
		)
	}

}

export default CourseDishonestyCharts