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
                    timeSpent: "12 hours",
                    id: 100
                },
                {
                    first_name: "Jordan",
                    last_name: "Reed",
                    progress: 100,
                    commitCount: 280,
                    timeSpent: "3 hours",
                    id: 101
                },
                {
                    first_name: "Killian",
                    last_name: "Le Clainche",
                    progress: 6.9,
                    commitCount: 69,
                    timeSpent: "6 hr 9 min",
                    id: 102
                },
                {
                    first_name: "Shawn",
                    last_name: "Montgomery",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours",
                    id: 103
                },
                {
                    first_name: "Jordan",
                    last_name: "Buckmaster",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours",
                    id: 104
                },
                {
                    first_name: "Jarett",
                    last_name: "Lee",
                    progress: 30,
                    commitCount: 12,
                    timeSpent: "12 hours",
                    id: 105
                }
            ]
        }
    }

    showProjectOptions = () => {
        history.push("/project-settings")
    };

    showStudentPanel = () => {
        history.push("/student");
    };

    render() {
        return (
            <div className="panel-course">
                <ProjectNavigation titleClick={this.showProjectOptions} mode={0} />
                <div className="panel-center-content">
                    <h3>Class Statistics</h3>
	                <ClassProgressHistogram/>
                    <h2 className="break-line" />
                    <h3>Students</h3>
                    <div className="panel-course-students">
                        {
                            this.state.student_data.map((student) => <Card key={student.id} component={<StudentPreview info={student} />} onClick={this.showStudentPanel}/>)
                        }
                    </div>
                </div>
            </div>
        )
    }
}

export default CoursePanel
