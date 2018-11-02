import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import {getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState, runTests, syncRepositories, updateStudentsPage, resetStudentsPage} from '../../redux/actions'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {CourseModal, CourseCharts, CourseStatistics, CourseStudentFilter} from './course'
import ActionNavigation from '../navigation/ActionNavigation'
import {Title, SettingsIcon, BackNav} from '../Helpers'
import CourseCommitHistory from './course/CourseCommitHistory'

class CoursePanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            filters: {
                sort_by: 0,
                order_by: 0,
                commit_filter: 0,
                hour_filter: 0,
                progress_filter: 0
            }
        }
    }

    componentDidMount = () => {
        this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&size=10&projectID=${this.props.currentProjectId}`)
    }

    scrolledToBottom = () => {
        if(!this.props.last) {
            this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&size=10&page=${this.props.page + 1}&projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy()}`)
            this.props.updateStudentsPage()
        }
    }

    getSortBy = (value) => {
        let id = value ? value : this.state.filters.sort_by
        switch(id) {
            case 0:
                return 'id'
            case 1:
                return 'timeSpent'
            case 2:
                return 'commitCounts'
            case 3:
                return 'grades'
            
        }
    }

    changeFilter = (key, value) => {
        console.log(key, value)
        this.state.filters[key] = value
        if(key === 'sort_by') {
            this.props.resetStudentsPage()
            this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&size=10&page=1&projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy(value)}`)
        }
        this.setState({ filters: Object.assign({}, this.state.filters) })
    }

    render() {

        const action_names = [
            'Manage Teaching Assistants',
            'Sync Project Repositories',
            'Run Project Tests',
            'Academic Dishonesty Report'
        ]

        const actions = [
            () => { history.push('/manage-tas') },
            () => {
                if(this.props.currentProjectId)
                    this.props.syncRepositories(`${url}/api/pull/project?projectID=${this.props.currentProjectId}`)
            },
            () => {
                if(this.props.currentProjectId)
                    this.props.runTests(`${url}/api/testall/project?projectID=${this.props.currentProjectId}`)
            },
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
                            <h4>Last Sync: {
                                this.props.projects && this.props.projects.length > 0 && this.props.projects[this.props.currentProjectIndex] ?
                                    this.props.projects[this.props.currentProjectIndex].last_sync
                                    : null
                            }
                            </h4>
                        </div>
                        <div>
                            <h4>Last Test Ran: {
                                this.props.projects && this.props.projects.length > 0  && this.props.projects[this.props.currentProjectIndex] ?
                                    this.props.projects[this.props.currentProjectIndex].last_test
                                    : null
                            }
                            </h4>
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

                        <div className='h1 break-line header' />
                        <h3 className='header'>Course Statistics</h3>
                        <CourseStatistics />

                        <div className='h1 break-line' />

                        <CourseStudentFilter onChange={ this.changeFilter } filters={ this.state.filters } />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
	    projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
	    currentProjectIndex: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        page: state.course && state.course.studentsPage ? state.course.studentsPage : 1,
        last: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.last : true,
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
        updateStudentsPage: () => dispatch(updateStudentsPage()),
        resetStudentsPage: () => dispatch(resetStudentsPage()),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CoursePanel)
