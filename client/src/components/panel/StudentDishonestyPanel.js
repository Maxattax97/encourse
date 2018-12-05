import React, { Component } from 'react'
import { connect } from 'react-redux'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav} from '../Helpers'
import {history} from '../../redux/store'
import {clearStudent, getStudent, syncStudentRepository, runStudentTests, setModalState} from '../../redux/actions'
import {getCurrentCourseId, getCurrentSemesterId} from '../../redux/state-peekers/course'
import url from '../../server'
import HistoryText from './common/HistoryText'
import StudentDishonestyCharts from "./student-dishonesty/StudentDishonestyCharts"
import ShareReportModal from "./common/ShareReportModal"
import {getCurrentStudent} from '../../redux/state-peekers/student'
import TaskModal from './common/TaskModal'
import {getCurrentProject} from '../../redux/state-peekers/projects'

class StudentDishonestyPanel extends Component {

    componentDidMount = () => {
        if(!this.props.student) {
            this.props.getStudent(`${url}/api/studentsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}&userName=${this.props.match.params.id}`)
        }
    }

    back = () => {
        history.goBack()
    }

    share = () => {
        this.props.setModalState(1)
    }

    render() {

        const action_names = [
            'Current Task',
            'Share Results'
        ]

        const actions = [
            () => { this.props.setModalState(2) },
            this.share
        ]
        //TODO: update currentStudent correctly
        return (
            <div className="student-dishonesty-panel">
                <div className='panel-left-nav'>
                    <BackNav back='Course' backClick={ this.back }/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className='panel-right-nav'>
                    <HistoryText />
                </div>

                <ShareReportModal id={1} link={null}/>
                <TaskModal id={2} />

                <div className='panel-center-content'>

                    <div className='panel-student-report'>
                        <h1 className='header'>'{ this.props.student ? this.props.student.last_name : 'Student' }' Dishonesty Report</h1>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Student Charts Summary</h3>
                        <StudentDishonestyCharts/>
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
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        syncStudentRepository: (url, headers, body) => dispatch(syncStudentRepository(url, headers, body)),
        runStudentTests: (url, headers, body) => dispatch(runStudentTests(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
	    setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentDishonestyPanel)