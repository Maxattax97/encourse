import React, { Component } from 'react'
import {Title} from '../Helpers'
import {getStudentPreviews, setCurrentStudent, setModalState, getDishonestyReport, updateCourseDishonestyPage, resetCourseDishonestyPage} from '../../redux/actions'
import {getCurrentCourseId, getStudents} from '../../redux/state-peekers/course'
import { connect } from 'react-redux'
import CourseDishonestyCharts from './course-dishonesty/CourseDishonestyCharts'
import ShareReportModal from './common/ShareReportModal'
import TaskModal from './common/TaskModal'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {getCurrentProject} from '../../redux/state-peekers/projects'
import BackNavigation from '../navigation/BackNavigation'
import {retrieveCourse} from '../../redux/retrievals/course'
import StudentReportFilter from './course-dishonesty/StudentReportFilter'
import {history} from '../../redux/store'
import ActionNavigation from '../navigation/ActionNavigation'

class CourseDishonestyPanel extends Component {

    constructor(props) {
        super(props)
    }

    share = () => {
        this.props.setModalState(2)
    }

    componentDidMount = () => {
        retrieveCourse(this.props.course)
    }

    render() {

        const action_names = [
            'Course Page'
        ]

        const actions = [
            () => { history.push(`/${this.props.course}/course`) },
        ]

        return (
            <div className="class-dishonesty-panel">

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <TaskModal id={1} />
                <ShareReportModal id={2} link={window.location}/>

                <div className='panel-center-content'>

                    <div className='panel-course-report'>
                        <Title>
                            <h1 className='header'>Academic Dishonesty Report</h1>
                        </Title>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Charts</h3>
                        <CourseDishonestyCharts/>

                        <StudentReportFilter/>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        course: getCurrentCourseId(state),
        project: getCurrentProject(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        getDishonestyReport: (url, headers, body) => dispatch(getDishonestyReport(url, headers, body)),
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CourseDishonestyPanel)