import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import { getStudentPreviews, getClassProjects, setCurrentProject, setCurrentStudent } from '../../redux/actions/index'
import ProjectNavigation from '../navigation/ProjectNavigation'
import Card from '../Card'
import StudentPreview from './util/StudentPreview'
import ClassProgressHistogram from '../charts/ClassProgressHistogram'
import ClassTestCasePercentDone from '../charts/ClassTestCasePercentDone'
import ActionNavigation from '../navigation/ActionNavigation'
import CourseModal from '../modals/CourseModal'
import { Title } from '../Helpers'
import { settings } from '../../helpers/icons'

import { fuzzing } from '../../fuzz'

class CoursePanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            modal_blur: ''
        }
    }

    componentDidMount = () => {
        //TODO: clear class projects/student previews to account for multiple classes
        //TODO: Add course ID functionality for multiple classes
        this.props.getClassProjects(`${url}/api/projectsData?courseID=cs252&semester=Fall2018`,
            {'Authorization': `Bearer ${this.props.token}`})
        this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018`,
            {'Authorization': `Bearer ${this.props.token}`})
    }

    showStudentPanel = (student) => {
        //TODO: move this setCurrentStudent to StudentPanel, store all students in an array in redux
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
                <div className={ this.state.show_course_options ? 'blur' : '' }>
                    <ProjectNavigation onModalBlur={(blur) => this.setState({modal_blur : blur ? ' blur' : ''})}
                        {...this.props}/>
                </div>

                <CourseModal show={ this.state.show_course_options }
                    close={ () => this.setState({ show_course_options: false, modal_blur: '' }) }/>

                <div className='panel-center-content'>

                    <div className={ `panel-course-content${this.state.modal_blur}` }>
                        <Title onClick={ () => this.setState({ show_course_options: true, modal_blur: ' blur' }) } header={ <h1 className='header'>CS252</h1> } icon={ settings } break />
                        <div className='h1 break-line header' />

                        <h3 className='header'>Course Charts</h3>
                        <div className='charts float-height'>
                            <Card component={<ClassProgressHistogram projectID={this.props.currentProjectId}/>} />
                            <Card component={<ClassTestCasePercentDone projectID={this.props.currentProjectId}/>} />
                        </div>

                        <div className='h1 break-line' />

                        <h3 className='header'>Students</h3>
                        <div className='panel-course-students float-height'>
                            {
                                this.props.students &&
                                this.props.students.map((student) =>
                                    <Card key={student.id}
                                        component={<StudentPreview student={student} projectID={this.props.currentProjectId}
                                            setCurrentProject={this.props.setCurrentProject} />}
                                        onClick={() => this.showStudentPanel(student)}/>)
                            }
                        </div>
                    </div>
                </div>

                <div className='panel-right-nav'>
                    <div className={ `panel-student-side-content${this.state.modal_blur}` }>
                        <ActionNavigation />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : 0
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        getClassProjects: (url, headers, body) => dispatch(getClassProjects(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursePanel)
