import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {getStudentPreviews, clearStudent} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {getCurrentCourseId, getCurrentSemesterId, getDishonestyReport} from '../../../redux/state-peekers/course'
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveDishonestyReport} from '../../../redux/retrievals/course'

class StudentReportSummary extends Component {

	componentDidMount() {
		if(this.props.project)
			retrieveDishonestyReport(this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveDishonestyReport(this.props.project)
	}

    clickStudentCard = (student) => {
	    this.props.clearStudent()
        history.push(`/${this.props.course}/${this.props.semester}/student-dishonesty/${student.id}`)
    }

	renderPreview = (student) => {
		return (
			<div>
				<Title>
					<h4>{ student.id }</h4>
				</Title>
				<div className="h4 break-line header" />
				<div className="preview-content">
                    <h5>Rate: { student.metrics.rate.percentile.toFixed(2) }</h5>
                    <h5>Velocity: {student.metrics.velocity.percentile.toFixed(2) }</h5>
					<h5>Score: { student.score.toFixed(2) }</h5>
				</div>
                <div className="student-preview-progress">
                    <div className="progress-bar">
                        <div style={
                            {
                                width: (student.score * 100.0) + '%',
                                background: student.score <= .75 ?
                                    '#4caf50'
                                    : student.score <= .9 ?
                                        '#FF7900'
                                        : '#FF2323'
                            }
                        } />
                    </div>
                    <h6 className="progress-text">
                        {
                            student.score <= .75 ?
                                'Low'
                                : student.score <= .9 ?
                                'Medium'
                                : 'High'
                        }
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
	                               values={this.props.report.data}
	                               render={this.renderPreview}
	                               onClick={this.clickStudentCard}
                                   noCheckmark />
        )
    }
}


const mapStateToProps = (state) => {
    return {
        project: getCurrentProject(state),
        report: getDishonestyReport(state),
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