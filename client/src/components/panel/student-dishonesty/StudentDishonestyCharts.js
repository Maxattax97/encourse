import React, { Component } from 'react'
import {ChartList} from '../../Helpers'
import VelocityPerCommit from "./chart/VelocityPerCommit"
import VelocityPerTime from "./chart/VelocityPerTime"
import PercentileRadar from './chart/PercentileRadar'

class StudentDishonestyCharts extends Component {

	render() {
		return (
		    <ChartList>
                <VelocityPerCommit/>
                <VelocityPerTime/>
                <PercentileRadar/>
            </ChartList>
		)
	}

}

export default StudentDishonestyCharts