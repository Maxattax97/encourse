import React, { Component } from 'react'

import '../../css/CoursePanel.css'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'

class CoursePanel extends Component {
    render() {
        return (
            <div className="CoursePanel">
                <Card />
                <Card />
                <Card />
            </div>
        )
    }
}

export default CoursePanel