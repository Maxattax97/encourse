import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {history} from '../../../redux/store'
import {
	getStudentPreviews,
	setCurrentStudent
} from '../../../redux/actions'
import SelectableCardSummary from "../common/SelectableCardSummary"

class StudentSummary extends Component {

	clickStudentCard = (student) => {

		this.props.setCurrentStudent(student)
		if (fuzzing) {
			history.push('/student/student')
		} else {
			history.push(`/student/${student.id}`)
		}
	};

	renderPreview = (student) => {
		return (
			<div>
				<Title>
					<h4>{ student.first_name }</h4>
					<h4>{ student.last_name }</h4>
				</Title>
				<div className="h4 break-line header" />
				<div className="preview-content">
					<h5>Time: { student.timeSpent[this.props.currentProjectId] } hours</h5>
					<h5>Commits: { student.commitCounts[this.props.currentProjectId] }</h5>
				</div>
				<div className="student-preview-progress">
					<div className="progress-bar">
						<div style={{width: (this.props.isHidden ? student.hiddenGrades[this.props.currentProjectId] : student.grades[this.props.currentProjectId]) + '%'}} />
					</div>
					<h6 className="progress-text">
						{parseInt(this.props.isHidden ? student.hiddenGrades[this.props.currentProjectId] : student.grades[this.props.currentProjectId])}%
					</h6>
				</div>
			</div>
		)
	}

	render() {
		return (
			<SelectableCardSummary
				type='students'
				values={this.props.students}
				render={this.renderPreview}
				onClick={this.clickStudentCard} />
		)
	}
}

const mapStateToProps = (state) => {
	return {
		isHidden: state.projects ? state.projects.isHidden : false,
		students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : [],
		currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
		getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
		setCurrentStudent: (student) => dispatch(setCurrentStudent(student))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentSummary)