import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {getStudentPreviews, clearStudent} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {getCurrentCourseId, getCurrentSemesterId} from "../../../redux/state-peekers/course"
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveAllStudents} from "../../../redux/retrievals/course"

class StudentReportSummary extends Component {

	componentDidMount() {
		if(this.props.project)
			retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
	}

    clickStudentCard = (student) => {
	    this.props.clearStudent()
        history.push(`/${this.props.course}/${this.props.semester}/student-dishonesty/${student.id}`)
    }

	renderPreview = (student) => {
	    const bar = Math.min(Math.max(0, student.score + 3) / 6.0, 1.0)

		return (
			<div>
				<Title>
					<h4>{ student.id }</h4>
				</Title>
				<div className="h4 break-line header" />
				<div className="preview-content">
					<h5>Score: { student.score.toFixed(2) }</h5>
				</div>
                <div className="student-preview-progress">
                    <div className="progress-bar">
                        <div style={
                            {
                                width: (bar * 100.0) + '%',
                                background: bar <= .67 ?
                                    '#008000'
                                    : bar <= .83 ?
                                        '#FF7900'
                                        : '#FF2323'
                            }
                        } />
                    </div>
                    <h6 className="progress-text">
                        {
                            bar <= .67 ?
                                'Low'
                                : bar <= .83 ?
                          -      'Medium'
                                : 'High'
                        }
                    </h6>
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
		course: getCurrentCourseId(state),
		semester: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentReportSummary)