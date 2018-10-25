import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import { getStudentPreviews, setCurrentProject, setCurrentStudent } from '../../redux/actions/index'
import ProjectNavigation from '../navigation/ProjectNavigation'
import StudentPreview from './util/StudentPreview'
import ClassProgressHistogram from '../chart/ClassProgressHistogram'
import ClassTestCasePercentDone from '../chart/ClassTestCasePercentDone'
import ActionNavigation from '../navigation/ActionNavigation'
import CourseModal from '../modal/CourseModal'
import {Title, Summary, Card, SettingsIcon,LoadingIcon} from '../Helpers'

import { fuzzing } from '../../fuzz'

class CoursePanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            modal_blur: ''
        }
    }

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
                <ProjectNavigation onModalBlur={ (blur) => this.setState({modal_blur : blur ? ' blur' : ''}) }
                    {...this.props}/>

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

                <CourseModal show={ this.state.show_course_options }
                    close={ () => this.setState({ show_course_options: false, modal_blur: '' }) }/>

                <div className='panel-center-content'>

                    <div className={ `panel-course-content${this.state.modal_blur}` }>
                        <Title onClick={ () => this.setState({ show_course_options: true, modal_blur: ' blur' }) }
                            header={ <h1 className='header'>CS252</h1> }
                            icon={ <SettingsIcon/> } />
                        <div className='h1 break-line header' />

                        <Summary header={ <h3 className='header'>Course Charts Summary</h3> }
                            columns={ 2 }
                            data={ [
                                <ClassProgressHistogram projectID={this.props.currentProjectId} key={1}/>,
                                <ClassTestCasePercentDone projectID={this.props.currentProjectId} key={2}/>
                            ] }
                            className='charts'
                            iterator={ (chart) => <Card key={ chart.key }>
                                {chart}
                            </Card> } />

                        <div className='h1 break-line' />

                        <Summary header={ <h3 className='header'>Students Summary</h3> }
                            columns={ 5 }
                            data={ this.props.students }
                            isLoading={ this.props.isLoading }
                            className='course-students'
                            iterator={ (student) =>
                                <StudentPreview key={ student.id }
                                    student={ student }
                                    projectID={ this.props.currentProjectId }
                                    onClick={ () => this.showStudentPanel(student) }/>
                            } />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        isLoading: state.course ? state.course.getStudentPreviewsIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursePanel)
