import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {getStudentPreviews, setCurrentStudent, clearStudent} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/project"
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveAllStudents} from "../../../redux/retrievals/course"

class StudentReportSummary extends Component {

	componentDidMount() {
		if(this.props.project)
			retrieveAllStudents(this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveAllStudents(this.props.project)
	}

    clickStudentCard = (student) => {
	    this.props.clearStudent()
        history.push(`/student-dishonesty/${student.id}`)
    }

	renderPreview = (student) => {
		return (
			<div>
				<Title>
					<h4>{ student.id }</h4>
				</Title>
				<div className="h4 break-line header" />
				<div className="preview-content">
					<h5>Score: { student.score }</h5>
				</div>
			</div>
		)
	}

    render() {
        return (
	        <SelectableCardSummary type='students'
	                               values={this.props.report}
	                               render={this.renderPreview}
	                               onClick={this.clickStudentCard} />
        )
    }
}


const mapStateToProps = (state) => {
    return {
        project: getCurrentProject(state),
        report: state.course && state.course.getDishonestyReportData ? state.course.getDishonestyReportData.content : [],

    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentReportSummary)