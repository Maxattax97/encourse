import React, { Component } from 'react'
import {Card, Summary} from '../../Helpers'
import ClassProgressHistogram from './chart/CourseCompletionProgress'
import ClassTestCasePercentDone from './chart/CourseTestCaseProgress'

class CourseCharts extends Component {
	render() {
		const chartList = [
			<ClassProgressHistogram key={1}/>,
			<ClassTestCasePercentDone key={2}/>
		]

		return (
			<Summary columns={ 2 } className='charts'>
				{
					chartList.map( (chart) =>
						<Card key={ chart.key }>
							{ chart }
						</Card>
					)
				}
			</Summary>
		)
	}
}

export default CourseCharts