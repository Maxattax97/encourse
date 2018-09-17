import React, { Component } from 'react'

import '../../css/CoursePanel.css'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'

class CoursePanel extends Component {
    render() {
        return (
                <div className="Course-Panel">
                    <div className="Grouping">
                        Settings
                    </div>
                    <div className="Course-Settings">
                        <div className="Break-Line" />
                        <table>
                            <tr>
                                <th>Project Name</th>
                                <th></th>
                            </tr>
                        </table>
                        <div className="Projects-List">
                            <div className="List">
                                <div className="Project">
                                    <div className=
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="Grouping">
                        Statistics
                    </div>
                    <div className="Grouping">
                        Students
                    </div>
                </div>
        )
    }
}

export default CoursePanel