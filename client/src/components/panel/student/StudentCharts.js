import React, { Component } from 'react'
import StudentProgressLineGraph from './chart/StudentProgressPerDay'
import CodeChangesChart from './chart/StudentCodeChanges'
import CommitFrequencyHistogram from './chart/StudentCommitsPerDay'
import ProgressPerTime from './chart/StudentVelocityPerTime'
import ProgressPerCommit from './chart/StudentVelocityPerCommit'
import {Card, Summary} from '../../Helpers'

class StudentFeedback extends Component {

    constructor(props) {
        super(props)

    }

    render() {

        const charts = [
            <StudentProgressLineGraph key={1}/>,
            <CodeChangesChart key={2}/>,
            <CommitFrequencyHistogram key={3}/>
        ]

        return (
            <Summary columns={ 2 } className='charts'>
                {
                    charts.map( (chart) =>
                        <Card key={ chart.key }>
                            { chart }
                        </Card>
                    )
                }
            </Summary>
        )
    }
}

export default StudentFeedback;
