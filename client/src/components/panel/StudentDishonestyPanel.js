import React, { Component } from 'react'
import { connect } from 'react-redux'
import ActionNavigation from '../navigation/ActionNavigation'
import {clearStudent, getStudent, syncStudentRepository, runStudentTests, setModalState} from '../../redux/actions'
import {getCurrentCourseId, getCurrentSemesterId} from '../../redux/state-peekers/course'
import StudentDishonestyCharts from "./student-dishonesty/StudentDishonestyCharts"
import ShareReportModal from "./common/ShareReportModal"
import {getCurrentStudent} from '../../redux/state-peekers/student'
import TaskModal from './common/TaskModal'
import {getCurrentProject} from '../../redux/state-peekers/projects'
import BackNavigation from '../navigation/BackNavigation'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {retrieveCourse} from '../../redux/retrievals/course'
import {retrieveStudent} from '../../redux/retrievals/student'
import StudentComparisonFilter from './student-dishonesty/StudentComparisonFilter'

class StudentDishonestyPanel extends Component {

    share = () => {
        this.props.setModalState(1)
    }

    componentDidMount = () => {
        retrieveCourse(this.props.currentCourseId)
        retrieveStudent(this.props.student.studentID)
    }

    componentWillUnmount() {
        this.props.clearStudent()
    }

    render() {

        const action_names = [
            //'Current Task',
            //'Share Results'
        ]

        const actions = [
            //() => { this.props.setModalState(2) },
            //this.share
        ]
        //TODO: update currentStudent correctly
        return (
            <div className="student-dishonesty-panel">
                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ProjectNavigation/>
                    {/*<ActionNavigation actions={ actions } action_names={ action_names }/>*/}
                </div>

                <ShareReportModal id={1} link={null}/>
                <TaskModal id={2} />

                <div className='panel-center-content'>

                    <div className='panel-student-report'>
                        <h1 className='header'>{ this.props.student ? `'${this.props.student.firstName} ${this.props.student.lastName}'` : '' } Dishonesty Report</h1>
                        <div className='h1 break-line header' />

                        {/*<h3 className='header'>Charts</h3>
                        <StudentDishonestyCharts/>*/}
                        <StudentComparisonFilter/>
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
        getStudent: (url, body) => dispatch(getStudent(url, {}, body)),
        syncStudentRepository: (url, headers, body) => dispatch(syncStudentRepository(url, headers, body)),
        runStudentTests: (url, headers, body) => dispatch(runStudentTests(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
	    setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentDishonestyPanel)