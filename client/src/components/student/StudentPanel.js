import React, { Component } from 'react'
import { connect } from 'react-redux'

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
            ],
            stats: [
                {
                    statname: "Estimated Time Spent",
                    statvalue: "5 hours"
                },
                {
                    statname: "Additions",
                    statvalue: "103"
                },
                {
                    statname: "Deletions",
                    statvalue: "3415"
                },
                {
                    statname: "Additions",
                    statvalue: "`35"
                },
                {
                    statname: "Deletions",
                    statvalue: "1234"
                },
                {
                    statname: "Additions",
                    statvalue: "123"
                },
                {
                    statname: "Deletions",
                    statvalue: "5342"
                },
                {
                    statname: "Additions",
                    statvalue: "213"
                },
                {
                    statname: "Deletions",
                    statvalue: "76"
                },
                {
                    statname: "Additions",
                    statvalue: "123"
                },
                {
                    statname: "Deletions",
                    statvalue: "567"
                },
                {
                    statname: "Additions",
                    statvalue: "43"
                },
                {
                    statname: "Deletions",
                    statvalue: "123"
                },
                {
                    statname: "Additions",
                    statvalue: "45"
                },
                {
                    statname: "Deletions",
                    statvalue: "36"
                }
            ]
        }
    }

    back = () => {
        history.push("/course");
    };

    showProjectOptions = () => {
        this.setState({project_options: !this.state.project_options, new_project: false})
    };

    updateProjectState = (project_index) => {
        this.setState({current_project: project_index})
    };

    newProject = () => {
        this.setState({ project_options: true, new_project: true });
    };

    createProject = () => {

    };

    deleteProject = () => {

    };

    changeProject = () => {

    };

    render() {
        return (
            <div className="panel-student">
                <ProjectNavigation titleClick={this.showProjectOptions} projectClick={this.updateProjectState} backClick={this.back}
                                   currentProject={this.state.current_project} info={this.state.projects} back="Course"
                                   newProjectClick={this.newProject} />
                <div className="panel-center-content">
                    <Modal left show={this.state.project_options} onClose={() => this.setState({project_options: false})}
                           component={<ProjectOptions projects={this.state.projects} current_project={this.state.current_project} new_project={this.state.new_project}
                                                      updateProject={{create: this.createProject, delete: this.deleteProject, change: this.changeProject}}
                                                      visible={this.state.project_options}/>}/>

                    <div className={"panel-student-content " + (this.state.project_options ? "blur" : "")}>
                        <h1>{this.props.currentStudent ? this.props.currentStudent.first_name + ' ' + this.props.currentStudent.last_name : ''}</h1>
                        <h1 className="break-line title" />
                        <h3>Charts</h3>
                        <div className="charts float-height">
                            <Card component={<StudentProgressLineGraph/>} />
                            <Card component={<CodeChangesChart/>} />
                            <Card component={<CommitFrequencyHistogram/>}/>
                        </div>
                        <h2 className="break-line" />
                        <div className="student-stats-comments float-height">
                            <Card component={
                                <div className="student-stats-container">
                                    <div className="title">
                                        <h3>Statistics</h3>
                                    </div>
                                    <h3 className="break-line title" />
                                    {
                                        this.state.stats.map((stat, index) =>
                                            <div className="student-stat">
                                                <div className="student-stat-content">
                                                    <h5>{stat.statname}</h5>
                                                    <h5>{stat.statvalue}</h5>
                                                </div>
                                                {
                                                    index % 2 === 1 && index !== this.state.stats.length - 1 ?
                                                        <h5 className="break-line" /> : null
                                                }
                                            </div>)
                                    }
                                </div>
                            } />
                            <Card component={
                                <div className="student-feedback-container">
                                    <div className="title">
                                        <h3>Feedback</h3>
                                    </div>
                                    <h3 className="break-line title" />
                                </div>
                            } />
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

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
    }
};
  
const mapDispatchToProps = (dispatch) => {
    return {

    }
};

export default connect(mapStateToProps, mapDispatchToProps)(StudentPanel)
