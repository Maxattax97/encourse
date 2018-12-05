import React, { Component } from 'react'
import StudentProgressLineGraph from './chart/StudentProgress'
import CodeChangesChart from './chart/CodeChanges'
import CommitFrequencyHistogram from './chart/CommitFrequency'
import {ChartList} from '../../Helpers'

class StudentFeedback extends Component {

    render() {

        return (
            <ChartList>
	            <StudentProgressLineGraph/>
	            <CodeChangesChart/>
	            <CommitFrequencyHistogram/>
            </ChartList>
        )
    }
}

export default StudentFeedback;
