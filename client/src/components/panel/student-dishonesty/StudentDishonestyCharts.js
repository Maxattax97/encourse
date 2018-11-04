import React, { Component } from 'react'
import {Card, Summary} from '../../Helpers'
import StudentVelocityPerCommit from "./chart/StudentVelocityPerCommit"
import StudentVelocityPerTime from "./chart/StudentVelocityPerTime"

class StudentDishonestyCharts extends Component {

	render() {
		return (
			<Summary columns={ 2 } className='charts'>
				<Card>
					<StudentVelocityPerCommit/>
				</Card>
				<Card>
					<StudentVelocityPerTime/>
				</Card>
			</Summary>
		)
	}

}

export default StudentDishonestyCharts