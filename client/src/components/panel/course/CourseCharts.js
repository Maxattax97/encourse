import React from 'react'
import ClassProgressHistogram from './chart/StudentsCompletionProgress'
import ClassTestCasePercentDone from './chart/StudentsTestCaseProgress'
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