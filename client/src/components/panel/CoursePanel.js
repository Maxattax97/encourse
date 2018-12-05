import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import { getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState, 
        runTests, syncRepositories } from '../../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from '../../redux/state-peekers/course'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {CourseModal, AnonymousCharts, Charts, CourseStatistics, CourseStudentFilter} from './course'
import ActionNavigation from '../navigation/ActionNavigation'
import HistoryText from './common/HistoryText'
import {Title, SettingsIcon, BackNav} from '../Helpers'
import ProgressModal from "./common/TaskModal"
import {isAnySelected} from '../../redux/state-peekers/control'

class CoursePanel extends Component {
    render() {

        const action_names = [
            'Manage Teaching Assistants',
            'View Current Task',
            'Academic Dishonesty Report'
        ]

        const actions = [
            () => { history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/manage-tas`) },
            () => { this.props.setModalState(2) },
            () => { history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course-dishonesty`) }
        ]

        return (
            <div className='panel-course'>

                <div className='panel-left-nav'>
                    <BackNav/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className='panel-right-nav'>
                    <HistoryText />
                </div>

                <CourseModal id={1}/>
				<ProgressModal id={2} />

                <div className='panel-center-content'>

                    <div className='panel-course-content'>
                        <Title onClick={ () => this.props.setModalState(1) }>
                            <h1 className='header'>{this.props.currentCourseId.toUpperCase()}</h1>
                            <SettingsIcon/>
                        </Title>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Course Charts Summary</h3>
                        <AnonymousCharts />

                        {
                            this.props.isAnySelected ?
                                <div>
                                    <div className='h1 break-line' />
                                    <h3 className='header'>Students Charts Summary</h3>
                                    <Charts/>
                                </div>
                                : null
                        }

                        <div className='h1 break-line' />
                        <h3 className='header'>Course Statistics</h3>
                        <CourseStatistics anon />

                        {
                            this.props.isAnySelected ?
                                <div>
                                    <div className='h1 break-line' />
                                    <h3 className='header'>Students Statistics</h3>
                                    <CourseStatistics />
                                </div>
                                : null
                        }


                        <div className='h1 break-line' />

                        <CourseStudentFilter />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
        isAnySelected: isAnySelected(state, 'students')
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        setModalState: (id) => dispatch(setModalState(id)),
        runTests: (url, headers, body) => dispatch(runTests(url, headers, body)),
        syncRepositories: (url, headers, body) => dispatch(syncRepositories(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CoursePanel)
