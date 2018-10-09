import React, { Component } from 'react'
import { connect } from 'react-redux'

import Card from '../Card'
import StudentProgressLineGraph from '../charts/StudentProgressLineGraph'
import CodeChangesChart from '../charts/CodeChangesChart'
import CommitFrequencyHistogram from '../charts/CommitFrequencyHistogram'
import ProjectNavigation from '../navigation/ProjectNavigation'
import Statistics from './util/Statistics'
import CommitHistory from './util/CommitHistory'
import { history } from '../../redux/store'
import { getClassProjects, clearStudent } from '../../redux/actions/index'
import url from '../../server'
import ActionNavigation from '../navigation/ActionNavigation'


class StudentPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            current_project : 2,
            projects : [
                {
                    name: 'My Malloc',
                    source_name: 'lab1-src',
                    created_date: '09-01-18',
                    due_date: '09-08-18',
                    test_script: true,
                    hidden_test_script: true,
                    id: 1 //id would be preferable for unique identification
                },
                {
                    name: 'Lab 2',
                    source_name: 'lab2-src',
                    created_date: '09-01-18',
                    due_date: '09-08-18',
                    test_script: true,
                    hidden_test_script: true,
                    id: 2
                },
                {
                    name: 'Shell Project',
                    source_name: 'lab3-src',
                    created_date: '09-01-18',
                    due_date: '09-08-18',
                    test_script: true,
                    hidden_test_script: true,
                    id: 3
                }
            ],
            modal_blur: ''
        }
    }

    componentWillMount = () => this.clear()
    componentWillUnmount = () => this.clear()
    clear = () => this.props.clearStudent()

    componentDidMount = () => {
        if(!this.props.projects) {
            //TODO: remove classid and semester hardcoding
            this.props.getClassProjects(`${url}/api/projectsData?courseID=cs252&semester=Fall2018`,
                {'Authorization': `Bearer ${this.props.token}`})
        }
    }

    back = () => {
        history.push('/course')
    };

    render() {
        return (
            <div className="panel-student">

                <ProjectNavigation
                    info={ this.props.projects }
                    back="Course"
                    backClick={ this.back }
                    onModalBlur={ (blur) => this.setState({modal_blur : blur ? ' blur' : ''}) }
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
                        <h3>Student Charts</h3>
                        <div className="charts float-height">
                            <Card component={<StudentProgressLineGraph projectID={this.props.currentProjectID} id={this.props.currentStudent.id}/>} />
                            <Card component={<CodeChangesChart projectID={this.props.currentProjectID} id={this.props.currentStudent.id}/>} />
                            <Card component={<CommitFrequencyHistogram projectID={this.props.currentProjectID} id={this.props.currentStudent.id}/>}/>
                        </div>
                        <h2 className="break-line" />
                        <div className="student-stats-comments float-height">
                            <Card component={<Statistics projectID={this.props.currentProjectID} id={this.props.currentStudent.id}/>}/>
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
                        <ActionNavigation />
                        <Card component={ <CommitHistory projectID={this.props.currentProjectID} id={this.props.currentStudent.id} /> } />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectID: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : 0
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers, body) => dispatch(getClassProjects(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentPanel)
