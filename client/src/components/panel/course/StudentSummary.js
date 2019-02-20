import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {
	setCurrentStudent,
	toggleFiltersHaveChanged
} from '../../../redux/actions'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {retrieveAllStudents} from '../../../redux/retrievals/course'
import {getCurrentProject} from '../../../redux/state-peekers/projects'
import {getStudents, getCurrentCourseId, getCurrentSemesterId} from '../../../redux/state-peekers/course'
import {getFilters, getFiltersHaveChanged} from '../../../redux/state-peekers/control'
import {hourMinutesFromMinutes} from '../../../common/time-helpers'

class StudentSummary extends Component {

    componentDidMount() {
        if(this.props.project)
            retrieveAllStudents(this.props.project)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project !== prevProps.project)
            retrieveAllStudents(this.props.project)
        /*if(!this.props.project)
            return;

        if(!(prevProps.project) || prevProps.project.index !== this.props.project.index) {
			retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
		} else if(this.props.filtersHaveChanged) {
			let body = {}
			let dynamicFilters = this.props.students && this.props.students.page ? this.props.students.page.filters : {}
			if(this.props.filters.order_by) {
				body.order = this.props.filters.order_by
			}
			if(this.props.filters.sort_by) {
				body.sortBy = this.getSortBy(this.props.filters.sort_by)
			}
			if(this.props.filters.commit_filter) {
				if(isNaN(this.props.filters.commit_filter)) {
					body.commitCounts = this.props.filters.commit_filter
				} else {
					body.commitCounts = {
						min: dynamicFilters.commitCounts[this.props.filters.commit_filter-1],
						max: dynamicFilters.commitCounts[this.props.filters.commit_filter],
					}
				}
			}

			if(this.props.filters.hour_filter) {
				if(isNaN(this.props.filters.hour_filter)) {
					body.timeSpent = this.props.filters.hour_filter
				} else {
					body.timeSpent = {
						min: dynamicFilters.timeSpent[this.props.filters.hour_filter-1],
						max: dynamicFilters.timeSpent[this.props.filters.hour_filter],
					}
				}

			}

			if(this.props.filters.progress_filter) {
				if(isNaN(this.props.filters.progress_filter)) {
					body.timeSpent = this.props.filters.progress_filter
				} else {
					body.grades = {
						min: dynamicFilters.grades[this.props.filters.progress_filter-1],
						max: dynamicFilters.grades[this.props.filters.progress_filter],
					}
				}
			}

			retrieveAllStudents(this.props.project, this.props.course, this.props.semester, body)
			this.props.toggleFiltersHaveChanged()
		}*/
	}

	getSortBy = (value) => {
        let id = value ? value : this.props.filters.sort_by
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
	

	clickStudentCard = (student) => {
	    this.props.setCurrentStudent(student)
	    history.push(`/${this.props.course}/student/${student.studentID}`)
	};

	renderPreview = (student) => {
	    const hasVisible = !this.props.filters.view_filter || this.props.filters.view_filter === 1
        const hasHidden = !this.props.filters.view_filter || this.props.filters.view_filter === 2
	    const totalPoints = (hasVisible && this.props.project.runTestall ? this.props.project.totalVisiblePoints : 0) + (hasHidden && this.props.project.runTestall ? this.props.project.totalHiddenPoints : 0)
        const points = (hasVisible ? student.visiblePoints : 0) + (hasHidden ? student.hiddenPoints : 0)

	    return (
	        <div>
	            <Title>
	                <h4>{ student.firstName }</h4>
	                <h4>{ student.lastName }</h4>
	            </Title>
	            <div className="h4 break-line header" />
	            <div className="preview-content">
	                <h5>Time: { hourMinutesFromMinutes(student.seconds) }</h5>
	                <h5>Commits: { student.commits }</h5>
	            </div>
	            <div className="student-preview-progress">
	                <div className="progress-bar">
	                    <div style={{width: (totalPoints <= 0.01 ? 100 : points / totalPoints * 100.0) + '%', background: this.runTestall ? "#4caf50" : "gray"}} />
	                </div>
	                <h6 className="progress-text">
                        {points}/{totalPoints}
	                </h6>
	            </div>
	        </div>
	    )
	}

	render() {
	    if(!this.props.project || !this.props.students.data)
	        return null

	    return (
	        <SelectableCardSummary type='students'
			                       values={this.props.students.data}
			                       render={this.renderPreview}
			                       onClick={this.clickStudentCard}
                                    noCheckmark={true}/>
	    )
	}
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        project: getCurrentProject(state),
        course: getCurrentCourseId(state),
		filters: getFilters(state),
		filtersHaveChanged: getFiltersHaveChanged(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
		setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
		toggleFiltersHaveChanged: () => dispatch(toggleFiltersHaveChanged())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentSummary)