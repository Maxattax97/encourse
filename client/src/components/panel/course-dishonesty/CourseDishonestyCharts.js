import React, { Component } from 'react'
import {Card, Summary} from '../../Helpers'
import CourseIdenticalLines from "./chart/CourseIdenticalLines"

class CourseDishonestyCharts extends Component {

	render() {
		return (
			<Summary columns={ 2 } className='charts'>
				<Card>
					<CourseIdenticalLines/>
				</Card>
			</Summary>
		)
	}

}

export default CourseDishonestyCharts