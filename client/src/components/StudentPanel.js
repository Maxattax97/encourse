import React, { Component } from 'react'

import Card from './Card'
import StudentProgressLineGraph from './charts/StudentProgressLineGraph'
import CodeChangesChart from './charts/CodeChangesChart'
import CommitFrequencyHistogram from './charts/CommitFrequencyHistogram'


class StudentPanel extends Component {

  render() {
    return (
      <div className="StudentPanel">
        <Card component={<StudentProgressLineGraph />}/>
        <Card component={<CodeChangesChart/>}/>
        <Card component={<CommitFrequencyHistogram/>}/>
      </div>
    )
  }
}

export default StudentPanel
