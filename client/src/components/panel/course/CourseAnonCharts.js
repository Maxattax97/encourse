import React from 'react'
import ClassProgressHistogram from './chart/CourseCompletionProgress'
import ClassTestCasePercentDone from './chart/CourseTestCaseProgress'
import ChartList from "../common/ChartList"

class CourseCharts extends ChartList {

	constructor(props) {
		super(props, [
			ClassProgressHistogram,
			ClassTestCasePercentDone
		])
	}
}

export default CourseCharts