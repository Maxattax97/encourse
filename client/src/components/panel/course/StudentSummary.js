import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {
	setCurrentStudent
} from '../../../redux/actions'
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveAllStudents} from "../../../redux/retrievals/course"
import {getCurrentProject} from "../../../redux/state-peekers/project"
import {getStudents} from "../../../redux/state-peekers/course"

class StudentSummary extends Component {

	componentWillReceiveProps(nextProps) {
		if(nextProps.project && (!(this.props.project) || this.props.project.index !== nextProps.project.index))
			retrieveAllStudents(nextProps.project)
	}

	clickStudentCard = (student) => {
		this.props.setCurrentStudent(student)
		history.push(`/student/${student.id}`)
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
					<h5>Time: { student.timeSpent[this.props.project.id] } hours</h5>
					<h5>Commits: { student.commitCounts[this.props.project.id] }</h5>
				</div>
				<div className="student-preview-progress">
					<div className="progress-bar">
						<div style={{width: (student.grades[this.props.project.id]) + '%'}} />
					</div>
					<h6 className="progress-text">
						{parseInt(student.grades[this.props.project.id])}%
					</h6>
				</div>
			</div>
		)
	}

	render() {
		if(!this.props.project)
			return null

		return (
			<SelectableCardSummary type='students'
			                       values={this.props.students}
			                       render={this.renderPreview}
			                       onClick={this.clickStudentCard} />
		)
	}
}

const mapStateToProps = (state) => {
	return {
		students: getStudents(state),
		project: getCurrentProject(state)
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
		setCurrentStudent: (student) => dispatch(setCurrentStudent(student))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentSummary)