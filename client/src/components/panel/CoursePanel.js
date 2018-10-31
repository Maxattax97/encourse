import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import {getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState} from '../../redux/actions/index'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {CourseModal, CourseCharts, CourseStudentFilter} from './course'
import ActionNavigation from '../navigation/ActionNavigation'
import {Title, SettingsIcon, BackNav} from '../Helpers'
import CourseCommitHistory from './course/CourseCommitHistory'

class CoursePanel extends Component {

    componentDidMount = () => {
        this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018`)
    }

    render() {

        const action_names = [
            'Manage Teaching Assistants',
            'Sync Repositories',
            'Run Tests',
            'Academic Dishonesty Report'
        ]

        const actions = [
            () => { history.push('/manage-tas') },
            () => {  },
            () => {  },
            () => { history.push('/course-dishonesty') }
        ]

        return (
            <div className='panel-course'>

                <div className='panel-left-nav'>
                    <BackNav/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className='panel-right-nav'>
                    <div className='top-nav'>
                        <div>
                            <h4>Last Sync:</h4>
                        </div>
                        <div>
                            <h4>Last Test Ran:</h4>
                        </div>
                    </div>
                    <CourseCommitHistory/>
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

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(null, mapDispatchToProps)(CoursePanel)
