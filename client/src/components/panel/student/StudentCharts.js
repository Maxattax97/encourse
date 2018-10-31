import React, { Component } from 'react'
import StudentProgressLineGraph from './chart/StudentProgressPerDay'
import CodeChangesChart from './chart/StudentCodeChanges'
import CommitFrequencyHistogram from './chart/StudentCommitsPerDay'
import {Card, Summary} from '../../Helpers'

class StudentFeedback extends Component {

    constructor(props) {
        super(props)

    }

    render() {

        return (
            <Summary columns={ 2 } className='charts'>
                <Card>
                    <StudentProgressLineGraph/>
                </Card>
                <Card>
                    <CodeChangesChart/>
                </Card>
                <Card>
                    <CommitFrequencyHistogram/>
                </Card>
            </Summary>
        )
    }
}

export default StudentFeedback;
