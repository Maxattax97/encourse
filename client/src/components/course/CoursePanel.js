import React, { Component } from 'react'

import ProjectNavigation from '../project/ProjectNavigation'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'
import settingsIcon from '../../img/settings.svg'
import { history } from '../../redux/store'
import ClassProgressHistogram from '../charts/ClassProgressHistogram'
import ProjectOptions from "../project/ProjectOptions";

class CoursePanel extends Component {

    constructor(props) {
        super(props);

        this.state = {
            current_project: props.project || 2,
            project_options: false,
            student_data : [
                {
                    first_name: "Ryan", //first name of student
                    last_name: "Sullivan", //last name of student
                    progress: [100, 100, 30], //array of size n of integers, n being the number of projects, containing the progress completion for each project
                    commitCount: [13, 12, 12], //array of size n of integers, n being the number of projects, containing the commit count for each project
                    timeSpent: ["12 hours", "12 hours", "12 hours"], //array of size n of strings, n being the number of projects, containing a formatted time spent for each project
                    id: 100 //PUID, but preferably something more unique when expanding to different colleges
                },
                {
                    first_name: "Jordan",
                    last_name: "Reed",
                    progress: [100, 100, 100],
                    commitCount: [280, 280, 280],
                    timeSpent: ["3 hours", "3 hours", "3 hours"],
                    id: 101
                },
                {
                    first_name: "Killian",
                    last_name: "Le Clainche",
                    progress: [6.9, 6.9, 6.9],
                    commitCount: [69, 69, 69],
                    timeSpent: ["6 hr 9 min", "6 hr 9 min", "6 hr 9 min"],
                    id: 102
                },
                {
                    first_name: "Shawn",
                    last_name: "Montgomery",
                    progress: [100, 100, 30],
                    commitCount: [13, 12, 12],
                    timeSpent: ["12 hours", "12 hours", "12 hours"],
                    id: 103
                },
                {
                    first_name: "Jordan",
                    last_name: "Buckmaster",
                    progress: [100, 100, 30],
                    commitCount: [13, 12, 12],
                    timeSpent: ["12 hours", "12 hours", "12 hours"],
                    id: 104
                },
                {
                    first_name: "Jarett",
                    last_name: "Lee",
                    progress: [100, 100, 30],
                    commitCount: [13, 12, 12],
                    timeSpent: ["12 hours", "12 hours", "12 hours"],
                    id: 105
                }
            ],
            projects : [
                {
                    name: "My Malloc",
                    src_name: "lab1-src",
                    created_date: "09-01-18",
                    due_date: "09-08-18",
                    test_script: true,
                    hidden_test_script: true,
                    id: 1 //id would be preferable for unique identification
                },
                {
                    name: "Lab 2",
                    src_name: "lab2-src",
                    created_date: "09-01-18",
                    due_date: "09-08-18",
                    test_script: true,
                    hidden_test_script: true,
                    id: 2
                },
                {
                    name: "Shell Project",
                    src_name: "lab3-src",
                    created_date: "09-01-18",
                    due_date: "09-08-18",
                    test_script: true,
                    hidden_test_script: true,
                    id: 3
                }
            ]
        }
    }

    showProjectOptions = () => {
        this.setState({project_options: !this.state.project_options})
    };

    showStudentPanel = () => {
        history.push("/student");
    };

    render() {
        return (
            <div className="panel-course">
                <ProjectNavigation titleClick={this.showProjectOptions} projectClick={(project) => this.setState({current_project: project})} info={this.state.projects} />
                {
                    this.state.project_options ?
                        <ProjectOptions onExit={() => this.setState({project_options: false})}/> :
                        <div className="panel-center-content">
                            <h3>Class Statistics</h3>
                            <ClassProgressHistogram/>
                            <h2 className="break-line" />
                            <h3>Students</h3>
                            <div className="panel-course-students">
                                {
                                    this.state.student_data.map((student) => <Card key={student.id} component={<StudentPreview info={student} project={this.state.current_project} />} onClick={this.showStudentPanel}/>)
                                }
                            </div>
                        </div>
                }

            </div>
        )
    }
}

export default CoursePanel
