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
import Statistics from './Statistics'
import {history} from "../../redux/store";
import CommitHistory from "./CommitHistory";


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
            modal_blur: ""
        }
    }

    back = () => {
        history.push("/course");
    };

    render() {
        return (
            <div className="panel-student">

                <ProjectNavigation
                    info={ this.state.projects }
                    back="Course"
                    backClick={ this.back }
                    onModalBlur={ (blur) => this.setState({modal_blur : blur ? " blur" : ""}) }
                    { ...this.props }/>

                <div className="panel-center-content">
                    <div className={ `panel-student-content${this.state.modal_blur}` }>
                        <h1>
                            {
                                this.props.currentStudent ?
                                    this.props.currentStudent.first_name + ' ' + this.props.currentStudent.last_name :
                                    ''
                            }
                        </h1>
                        <h1 className="break-line title" />
                        <h3>Charts</h3>
                        <div className="charts float-height">
                            <Card component={<StudentProgressLineGraph/>} />
                            <Card component={<CodeChangesChart/>} />
                            <Card component={<CommitFrequencyHistogram/>}/>
                        </div>
                        <h2 className="break-line" />
                        <div className="student-stats-comments float-height">
                            <Card component={<Statistics />}/>
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
                        <Card component={ <CommitHistory /> } />
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
