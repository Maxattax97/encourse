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
                    stat_name: "Estimated Time Spent",
                    stat_value: "5 hours"
                },
                {
                    stat_name: "Additions",
                    stat_value: "103"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "3415"
                },
                {
                    stat_name: "Additions",
                    stat_value: "`35"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "1234"
                },
                {
                    stat_name: "Additions",
                    stat_value: "123"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "5342"
                },
                {
                    stat_name: "Additions",
                    stat_value: "213"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "76"
                },
                {
                    stat_name: "Additions",
                    stat_value: "123"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "567"
                },
                {
                    stat_name: "Additions",
                    stat_value: "43"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "123"
                },
                {
                    stat_name: "Additions",
                    stat_value: "45"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "36"
                }
            ],
            modal_blur: ""
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
                <ProjectNavigation info={this.state.projects} onModalBlur={(blur) => this.setState({modal_blur : blur ? " blur" : ""})} {...this.props}/>
                <div className="panel-center-content">
                    <div className={ `panel-student-content${this.state.modal_blur}` }>
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
                                                    <h5>{stat.stat_name}</h5>
                                                    <h5>{stat.stat_value}</h5>
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
                    <div className={ `panel-student-side-content${this.state.modal_blur}` }>
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
