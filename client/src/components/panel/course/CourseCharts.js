import React, {Component} from 'react'
import CompletionProgress from './chart/CompletionProgress'
import TestCaseProgress from './chart/TestCaseProgress'
import {ChartList} from "../../Helpers"

class CourseCharts extends Component {

	render() {
		return (
			<ChartList>
				<CompletionProgress/>
			</ChartList>
		)
	}
}

export default CourseCharts