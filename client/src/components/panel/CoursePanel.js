import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import { getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState, 
        runTests, syncRepositories } from '../../redux/actions'
import {getCourse, getCurrentCourseId, getCurrentSemesterId} from '../../redux/state-peekers/course'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {CourseModal, AnonymousCharts, Charts, CourseStatistics, CourseStudentFilter} from './course'
import ActionNavigation from '../navigation/ActionNavigation'
import {Summary, Title} from '../Helpers'
import ProgressModal from "./common/TaskModal"
import {isAnySelected} from '../../redux/state-peekers/control'
import CustomRangeModal from './common/CustomRangeModal'
import BackNavigation from '../navigation/BackNavigation'
import {getAccount} from '../../redux/state-peekers/auth'
import {isAccountNotTA} from '../../common/state-helpers'
import {retrieveCourse} from '../../redux/retrievals/course'

class CoursePanel extends Component {

    componentDidMount = () => {
        window.scrollTo(0, 0);

        retrieveCourse(this.props.currentCourseId)
    }

    render() {

        const action_names = /*isAccountNotTA(this.props.account) ? [
            'Manage Teaching Assistants',
            /*'View Current Task',*
            'Academic Dishonesty Report'
        ] : */[
            /*'View Current Task',*/
            'Academic Dishonesty Report'
        ]

        const actions = /*isAccountNotTA(this.props.account) ? [
            () => { history.push(`/${this.props.currentCourseId}/manage-tas`) },
            () => { this.props.setModalState(2) },
            () => { history.push(`/${this.props.currentCourseId}/course-dishonesty`) }
        ] : */[
            /*() => { this.props.setModalState(2) },*/
            () => { history.push(`/${this.props.currentCourseId}/course-dishonesty`) }
        ]

        return (
            <div className='panel-course'>

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <CourseModal id={1}/>
                {/*<ProgressModal id={2} />*/}
                <CustomRangeModal />

                <div className='panel-center-content'>

                    <div className='panel-course-content'>
                        {
                            isAccountNotTA(this.props.account) ?
                                <Title onClick={ () => this.props.setModalState(1) }>
                                    <h1 className='header'>Course</h1>
                                </Title>
                                :
                                <Title>
                                    <h1 className='header'>Course</h1>
                                </Title>
                        }
                        <div className='h1 break-line header' />

                        <h3 className='header'>Charts</h3>
                        <Charts />

                        {/*<div className='h1 break-line' />
                        <h3 className='header'>Statistics</h3>
                        <Summary columns={2}>
                            <CourseStatistics anon />
                            {
                                this.props.isAnySelected && false ?
                                    <CourseStatistics />
                                    : null
                            }
                        </Summary>*/}

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
        isAnySelected: isAnySelected(state, 'students'),
        account: getAccount(state),
        course: getCourse(state)
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
