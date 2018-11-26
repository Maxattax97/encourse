import React, { Component } from 'react'
import { connect } from 'react-redux'

import {BackNav} from '../Helpers'
import ProjectNavigation from '../navigation/ProjectNavigation'
import { history } from '../../redux/store'
import { getStudent, clearStudent, runStudentTests, syncStudentRepository } from '../../redux/actions/index'
import url from '../../server'
import ActionNavigation from '../navigation/ActionNavigation'
import {StudentCharts, StudentCommitHistory, StudentStatistics} from './student'
import SyncItem from './common/HistoryText'
import { fuzzing } from '../../fuzz'


class StudentPanel extends Component {

    componentDidMount = () => {
        if(!this.props.currentStudent) {
            this.props.getStudent(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&userName=${this.props.match.params.id}`)
        }
    }

	componentWillUnmount() {
		this.props.clearStudent()
	}

	back = () => {
        history.goBack()
    }

    render() {

        const action_names = [
            'Sync Repository',
            'Run Tests',
            'Academic Dishonesty Report'
        ]

        let studentDishonestyRedirect = () => { history.push('/student-dishonesty/' + this.props.currentStudent.id) }
        if (fuzzing) {
            studentDishonestyRedirect = () => { history.push('/student-dishonesty/student') }
        }

        const actions = [
            () => { 
                if(this.props.currentProjectId && this.props.currentStudent)
                    this.props.syncStudentRepository(`${url}/api/pull/project?projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            },
            () => {
                if(this.props.currentProjectId && this.props.currentStudent)
                    this.props.runStudentTests(`${url}/api/run/testall?projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            },
            studentDishonestyRedirect
        ]

        return (
            <div className="panel-student">

                <div className='panel-left-nav'>
                    <BackNav back="Course"
                        backClick={ this.back }/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className="panel-right-nav">
                    <SyncItem />
                    <StudentCommitHistory />
                </div>

                <div className="panel-center-content">
                    <div className='panel-student-content'>
                        <h1 className='header'>
                            {
                                `CS252 - ${this.props.currentStudent ? `${this.props.currentStudent.first_name} ${this.props.currentStudent.last_name}` : ''}`
                            }
                        </h1>
                        <div className="h1 break-line header" />

                        <h3 className='header'>Student Charts</h3>
                        <StudentCharts />

                        <div className="h1 break-line" />

                        <h3 className='header'>Student Statistics</h3>
	                    <StudentStatistics/>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        syncStudentRepository: (url, headers, body) => dispatch(syncStudentRepository(url, headers, body)),
        runStudentTests: (url, headers, body) => dispatch(runStudentTests(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentPanel)
