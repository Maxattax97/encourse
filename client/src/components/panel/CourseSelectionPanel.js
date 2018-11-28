import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import {getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState, runTests, syncRepositories, updateStudentsPage, resetStudentsPage} from '../../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from '../../redux/state-peekers/course'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {CourseModal, AnonymousCharts, Charts, CourseStatistics, CourseStudentFilter} from './course'
import ActionNavigation from '../navigation/ActionNavigation'
import HistoryText from './common/HistoryText'
import {Title, SettingsIcon, BackNav} from '../Helpers'
import ProgressModal from "./common/ProgressModal"
import {retrieveAllStudents} from "../../redux/retrievals/course"

class CourseSelectionPanel extends Component {

	componentDidMount = () => {
		this.props.getStudentPreviews(`${url}/api/studentsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}&size=10&projectID=${this.props.currentProjectId}`)
	}

	scrolledToBottom = () => {
		if(!this.props.last) {
			this.props.getStudentPreviews(`${url}/api/studentsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}&size=10&page=${this.props.page + 1}&projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy()}`)
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
			default:
				return 'id'

		}
	}

	changeFilter = (key, value) => {
		const filters = [...this.state.filters]
		filters[key] = value
		this.setState({ filters }, () => {
			this.props.resetStudentsPage()
			this.props.getStudentPreviews(`${url}/api/studentsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}&size=10&page=1&projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy()}&order=${this.state.filters.order_by}&commit=${this.state.filters.commit_filter}&progress=${this.state.filters.progress_filter}&hour=${this.state.filters.hour_filter}`)
		})
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
				this.props.setModalState(2)

				if(this.props.currentProjectId)
					this.props.syncRepositories(`${url}/api/pull/project?projectID=${this.props.currentProjectId}`)
			},
			() => {
				this.props.setModalState(3)

				if(this.props.currentProjectId)
					this.props.runTests(`${url}/api/run/testall?projectID=${this.props.currentProjectId}`)
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
					<HistoryText />
				</div>

				<CourseModal id={1}/>
				<ProgressModal id={2} header={'Syncing'} progress={5} />
				<ProgressModal id={3} header={'Running Tests'} progress={5} />

				<div className='panel-center-content'>

					<div className='panel-course-content'>
						<Title onClick={ () => this.props.setModalState(1) }>
							<h1 className='header'>{this.props.currentCourseId.toUpperCase()}</h1>
							<SettingsIcon/>
						</Title>
						<div className='h1 break-line header' />

						<h3 className='header'>Course Charts Summary</h3>
						<AnonymousCharts />

						<div className='h1 break-line' />

						<h3 className='header'>Students Charts Summary</h3>
						<Charts/>

						<div className='h1 break-line' />
						<h3 className='header'>Course Statistics</h3>
						<CourseStatistics />

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
		projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
		currentProjectIndex: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0,
		currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
		currentCourseId: getCurrentCourseId(state),
		currentSemesterId: getCurrentSemesterId(state),
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

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CourseSelectionPanel)
