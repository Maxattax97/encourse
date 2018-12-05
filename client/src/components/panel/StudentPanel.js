import React, { Component } from 'react'
import { connect } from 'react-redux'

import {BackNav} from '../Helpers'
import ProjectNavigation from '../navigation/ProjectNavigation'
import { history } from '../../redux/store'
import {
    getStudent,
    clearStudent,
    runStudentTests,
    syncStudentRepository,
    setModalState
} from '../../redux/actions/index'
import ActionNavigation from '../navigation/ActionNavigation'
import {StudentCharts, StudentCommitHistory, StudentStatistics} from './student'
import SyncItem from './common/HistoryText'
import {retrieveStudent} from "../../redux/retrievals/student"
import {getCurrentStudent} from "../../redux/state-peekers/student"
import {getCurrentProject} from "../../redux/state-peekers/projects"
import {getCurrentCourseId, getCurrentSemesterId} from "../../redux/state-peekers/course"
import ProgressModal from './common/TaskModal'


class StudentPanel extends Component {

    componentDidMount = () => {
        if(!this.props.currentStudent) {
            retrieveStudent({id: this.props.match.params.id}, this.props.currentCourseId, this.props.currentSemesterId)
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
            'View Current Task',
            'Academic Dishonesty Report'
        ]

        let studentDishonestyRedirect = () => { history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/student-dishonesty/${this.props.student.id}`)}

        const actions = [
            () => {
                this.props.setModalState(2)
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

                <ProgressModal id={2} />

                <div className="panel-center-content">
                    <div className='panel-student-content'>
                        <h1 className='header'>
                            {
                                 this.props.student ? `${this.props.student.first_name} ${this.props.student.last_name}` : ''
                            }
                        </h1>
                        <div className="h1 break-line header" />

                        <h3 className='header'>Student Charts</h3>
                        <StudentCharts />

                        <div className="h1 break-line" />
                        <h3 className='header'>Student Statistics</h3>
	                    <StudentStatistics />

                        <div className='h1 break-line' />
                        <h3 className='header'>Test Suite Results</h3>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setModalState: (id) => dispatch(setModalState(id)),
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        syncStudentRepository: (url, headers, body) => dispatch(syncStudentRepository(url, headers, body)),
        runStudentTests: (url, headers, body) => dispatch(runStudentTests(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentPanel)
