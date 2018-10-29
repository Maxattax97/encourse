import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import {getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState} from '../../redux/actions/index'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {CourseModal, StudentPreview, CourseCharts, CourseStudentFilter} from './course'
import ActionNavigation from '../navigation/ActionNavigation'
import {Title, SettingsIcon} from '../Helpers'

import { fuzzing } from '../../fuzz'

class CoursePanel extends Component {

    componentDidMount = () => {
        this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018`)
    }

    showStudentPanel = (student) => {
        this.props.setCurrentStudent(student)
        if (fuzzing) {
            // NOTE: we don't even use the student id in the url
            history.push('/student/student')
        } else {
            history.push(`/student/${student.id}`)
        }
    };

    render() {

        return (
            <div className='panel-course'>
                <ProjectNavigation {...this.props} />

                <div className='panel-right-nav'>
                    <div className='top-nav'>
                        <div className='course-repository-info'>
                            <h4>Last Sync:</h4>
                            <h4>Last Test Ran:</h4>
                        </div>
                    </div>
                    <ActionNavigation actions={[
                        () => { history.push('/manage-tas') },
                        () => {  },
                        () => {  },
                        () => { history.push('/course-dishonesty') }
                    ]}
                    action_names={[
                        'Manage Teaching Assistants',
                        'Sync Repositories',
                        'Run Tests',
                        'Academic Dishonesty Report'
                    ]}/>

                </div>

                <CourseModal id={1}/>

                <div className='panel-center-content'>

                    <div className='panel-course-content'>
                        <Title onClick={ () => this.props.setModalState(1) }>
                            <h1 className='header'>CS252</h1>
                            <SettingsIcon/>
                        </Title>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Course Charts Summary</h3>

                        <CourseCharts/>

                        <div className='h1 break-line' />

                        <h3 className='header'>Students Summary</h3>
                        <CourseStudentFilter />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursePanel)
