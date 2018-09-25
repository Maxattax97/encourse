import React, { Component } from 'react'

import ProjectNavigation from '../project/ProjectNavigation'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'
import settingsIcon from '../../img/settings.svg'
import { history } from '../../redux/store'
import ClassProgressHistogram from '../charts/ClassProgressHistogram'

class CoursePanel extends Component {

    constructor(props) {
        super(props);

        this.state = {
            student_data : [
                {
                    first_name: "Ryan",
                    last_name: "Sullivan",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours"
                },
                {
                    first_name: "Ryan",
                    last_name: "Sullivan",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours"
                },
                {
                    first_name: "Ryan",
                    last_name: "Sullivan",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours"
                },
                {
                    first_name: "Ryan",
                    last_name: "Sullivan",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours"
                },
                {
                    first_name: "Ryan",
                    last_name: "Sullivan",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours"
                }
            ]
        }
    }

    showProjectOptions = () => {
        history.push("/project-settings")
    }

    render() {
        return (
            <div className="panel-course">
                <ProjectNavigation titleClick={this.showProjectOptions} mode={0} />
                <div className="panel-center-content">
                    <h3>Class Statistics</h3>
	                <ClassProgressHistogram/>
                    <h2 className="break-line title" />
                    <div className="panel-course-students">
                        {
                            this.state.student_data.map((student) => <Card component={<StudentPreview info={student} />} />)
                        }
                    </div>
                </div>
            </div>
        )
    }
}

export default CoursePanel
