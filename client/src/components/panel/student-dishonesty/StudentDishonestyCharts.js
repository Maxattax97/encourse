import React, { Component } from 'react'
import {Card, Summary} from '../../Helpers'
import VelocityPerCommit from "./chart/VelocityPerCommit"
import VelocityPerTime from "./chart/VelocityPerTime"

class StudentDishonestyCharts extends Component {

	render() {
		return (
			<Summary columns={ 2 } className='charts'>
				<Card>
					<VelocityPerCommit/>
				</Card>
				<Card>
					<VelocityPerTime/>
				</Card>
			</Summary>
		)
	}

}

export default StudentDishonestyCharts