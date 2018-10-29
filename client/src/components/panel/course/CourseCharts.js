import React, { Component } from 'react'
import {Card, Summary} from '../../Helpers'
import ClassProgressHistogram from '../../chart/ClassProgressHistogram'
import ClassTestCasePercentDone from '../../chart/ClassTestCasePercentDone'

class CourseCharts extends Component {
    render() {
        const chartList = [
            <ClassProgressHistogram key={1}/>,
            <ClassTestCasePercentDone key={2}/>
        ]

        return (
            <Summary
                columns={ 2 }
                data={ chartList }
                className='charts'
                iterator={ (chart) => <Card key={ chart.key }>
                    { chart }
                </Card> } >
            </Summary>
        )
    }
}

export default CourseCharts