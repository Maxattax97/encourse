import React, { Component } from 'react'

import Card from './Card'
import StudentProgressLineGraph from './charts/StudentProgressLineGraph'


class StudentPanel extends Component {

  render() {
    return (
      <div className="StudentPanel"> 
        <Card component={<StudentProgressLineGraph />}/>
      </div>
    )
  }
}

export default StudentPanel