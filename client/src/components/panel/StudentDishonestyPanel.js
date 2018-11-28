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

class StudentDishonestyPanel extends Component {

    componentDidMount = () => {
        if(!this.props.currentStudent) {
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
            'Sync Repository',
            'Run Tests',
            'Share Results'
        ]

        const actions = [
            () => { 
                if(this.props.currentProjectId && this.props.currentStudent)
                    this.props.syncStudentRepository(`${url}/api/pull/project?projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            },
            () => {
                if(this.props.currentProjectId && this.props.currentStudent)
                    this.props.runStudentTests(`${url}/api/run/testall?projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            },
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

                <div className='panel-center-content'>

                    <div className='panel-student-report'>
                        <h1 className='header'>{this.props.currentCourseId.toUpperCase()} - { this.props.currentStudent ? this.props.currentStudent.first_name : '' } { this.props.currentStudent ? this.props.currentStudent.last_name : '' } - Academic Dishonesty Report</h1>
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
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
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