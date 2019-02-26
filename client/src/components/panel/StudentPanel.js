import React, { Component } from 'react'
import { connect } from 'react-redux'

import ProjectNavigation from '../navigation/ProjectNavigation'
import { history } from '../../redux/store'
import {
    getStudent,
    clearStudent,
    runStudentTests,
    syncStudentRepository,
    setModalState, setCurrentStudent
} from '../../redux/actions/index'
import ActionNavigation from '../navigation/ActionNavigation'
import {StudentCharts, StudentProjectInfo} from './student'
import StudentChartSlider from './student/chart/BrushSlider'
import {getCurrentStudent} from "../../redux/state-peekers/student"
import {getCurrentProject} from "../../redux/state-peekers/projects"
import {getCurrentCourseId, getCurrentSemesterId} from "../../redux/state-peekers/course"
import ProgressModal from './common/TaskModal'
import BackNavigation from '../navigation/BackNavigation'
import {Summary} from '../Helpers'
import StudentSuiteInfo from './student/StudentSuiteInfo'
import StudentCommitFilter from './student/StudentCommitFilter'
import {retrieveStudent} from '../../redux/retrievals/student'
import {retrieveCourse} from '../../redux/retrievals/course'


class StudentPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            clear: true
        }
    }

    componentDidMount = () => {
        retrieveCourse(this.props.course)
        retrieveStudent(this.props.student.studentID)
    }

	componentWillUnmount() {
        if(this.state.clear)
		    this.props.clearStudent()
	}

    render() {

        const action_names = [
            'Student Dishonesty Page',
            'Course Page'
        ]

        const actions = [
            () => {
                this.setState({
                    clear: false
                })
                this.props.setCurrentStudent(this.props.student)
                history.push(`/${this.props.course}/student-dishonesty/${this.props.student.studentID}`)
            },
            () => {
                history.push(`/${this.props.course}/course`)
            }
        ]

        return (
            <div className="panel-student">

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <ProgressModal id={2} />

                <div className="panel-center-content">
                    <div className='panel-student-content'>
                        <h1 className='header'>
                            {
                                 this.props.student ? `${this.props.student.firstName} ${this.props.student.lastName}` : ''
                            }
                        </h1>
                        <div className="h1 break-line header" />

                        <h3 className='header'>Charts</h3>
                        <StudentCharts />

                        <div className='h1 break-line' />
                        <StudentCommitFilter/>

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
        course: getCurrentCourseId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setModalState: (id) => dispatch(setModalState(id)),
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        syncStudentRepository: (url, headers, body) => dispatch(syncStudentRepository(url, headers, body)),
        runStudentTests: (url, headers, body) => dispatch(runStudentTests(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentPanel)
