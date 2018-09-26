import React, { Component } from 'react'

import Card from '../Card'
import StudentProgressLineGraph from '../charts/StudentProgressLineGraph'
import CodeChangesChart from '../charts/CodeChangesChart'
import CommitFrequencyHistogram from '../charts/CommitFrequencyHistogram'
import ProjectNavigation from "../project/ProjectNavigation";
import Modal from "../Modal";
import ProjectOptions from "../project/ProjectOptions";
import ClassProgressHistogram from "../charts/ClassProgressHistogram";
import StudentPreview from "../course/StudentPreview";
import {history} from "../../redux/store";


class StudentPanel extends Component {

    constructor(props) {
        super(props);

        this.state = {
            current_project : 2,
            projects : [
                {
                    name: "My Malloc",
                    source_name: "lab1-src",
                    created_date: "09-01-18",
                    due_date: "09-08-18",
                    test_script: true,
                    hidden_test_script: true,
                    id: 1 //id would be preferable for unique identification
                },
                {
                    name: "Lab 2",
                    source_name: "lab2-src",
                    created_date: "09-01-18",
                    due_date: "09-08-18",
                    test_script: true,
                    hidden_test_script: true,
                    id: 2
                },
                {
                    name: "Shell Project",
                    source_name: "lab3-src",
                    created_date: "09-01-18",
                    due_date: "09-08-18",
                    test_script: true,
                    hidden_test_script: true,
                    id: 3
                }
            ]

        }
    }

    back = () => {
        history.push("/course");
    };

    showProjectOptions = () => {
        this.setState({project_options: !this.state.project_options})
    };

    updateProjectState = (project_index) => {
        this.setState({current_project: project_index})
    };

    render() {
        return (
            <div className="panel-student">
                <ProjectNavigation titleClick={this.showProjectOptions} projectClick={this.updateProjectState} backClick={this.back}
                                   currentProject={this.state.current_project} info={this.state.projects} back="Course" />
                <div className="panel-center-content">
                    <Modal left show={this.state.project_options} onClose={() => this.setState({project_options: false})}
                           component={<ProjectOptions project={this.state.projects[this.state.current_project]}/>}/>

                    <div className={"panel-student-content " + (this.state.project_options ? "blur" : "")}>
                        <h1>Jordan Reed</h1>
                        <h1 className="break-line title" />
                        <h3>Statistics</h3>
                        <div className="charts float-height">
                            <Card component={<StudentProgressLineGraph/>} />
                            <Card component={<CodeChangesChart/>} />
                            <Card component={<CommitFrequencyHistogram/>}/>
                        </div>
                        <h2 className="break-line" />
                        <div className="student-commits-feedback">
                            <div className="student-commits">
                            </div>
                            <div className="student-feedback">
                            </div>
                        </div>
                    </div>
                </div>
                <div className="panel-right-nav">
                    <div className={"panel-student-side-content " + (this.state.project_options ? "blur" : "")}>
                        <Card component={
                            <div className="student-actions-container">
                                <div className="title">
                                    <h3>Actions</h3>
                                </div>
                                <h3 className="break-line title" />
                                <h4 className="student-action-test">
                                    Run Tests
                                </h4>
                                <h4 className="student-action-hidden-test">
                                    Run Hidden Tests
                                </h4>
                                <h4 className="student-action-pull-repo">
                                    Pull Repository
                                </h4>
                                <h4 className="student-action-view-report">
                                    View Plagiarism Report
                                </h4>
                            </div>
                        } />
                    </div>
                </div>
            </div>
        )
    }
}

export default StudentPanel
