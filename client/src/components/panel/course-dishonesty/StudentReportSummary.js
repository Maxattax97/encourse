import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {getStudentPreviews, clearStudent, setCurrentStudent} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {
    getCurrentCourseId,
    getStudents
} from '../../../redux/state-peekers/course'
import SelectableCardSummary from "../common/SelectableCardSummary"
import {
    retrieveAllStudentReports,
} from '../../../redux/retrievals/course'

class StudentReportSummary extends Component {

    componentDidMount() {
        if(this.props.project)
            retrieveAllStudentReports(this.props.project)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project !== prevProps.project)
            retrieveAllStudentReports(this.props.project)
	}

    clickStudentCard = (student) => {
        this.props.setCurrentStudent(student)
        history.push(`/${this.props.course}/student-dishonesty/${student.studentID}`)
    }

	renderPreview = (student) => {
        if(!student.timeVelocity && student.timeVelocity !== 0)
            return null;

		return (
			<div>
				<Title>
					<h4>{ student.lastName }</h4>
				</Title>
				<div className="h4 break-line header" />
				<div className="preview-content">
                    <h5>Changes: { student.changes.toFixed(2) }</h5>
                    <h5>Time Velocity: {student.timeVelocity.toFixed(2) }</h5>
                    <h5>Commit Velocity: {student.commitVelocity.toFixed(2) }</h5>
					<h5>Comparison: { student.comparison } ({ student.comparisonPercent.toFixed(2) })</h5>
				</div>
			</div>
		)
	}

    render() {
	    if(!this.props.project)
	        return null

        return (
	        <SelectableCardSummary type='students'
	                               values={this.props.students.data}
	                               render={this.renderPreview}
	                               onClick={this.clickStudentCard}
                                   noCheckmark />
        )
    }
}


const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        project: getCurrentProject(state),
        course: getCurrentCourseId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentReportSummary)