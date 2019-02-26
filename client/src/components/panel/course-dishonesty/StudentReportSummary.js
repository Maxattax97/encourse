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
import {getFilters, getFiltersHaveChanged} from '../../../redux/state-peekers/control'

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

	collect = () => {
        if(this.props.students.isLoading || this.props.students.error || !this.props.students.data || !this.props.students.data.map)
            return [];

        return this.props.students.data.sort((a, b) => {
            const order = this.props.filters.order_by === 0 ? 1 : -1;

            let compA;
            let compB;

            switch(this.props.filters.sort_by) {
                case 0:
                    compA = a.lastName;
                    compB = b.lastName;
                    break;
                case 1:
                    compA = a.changes;
                    compB = b.changes;
                    break;
                case 2:
                    compA = a.timeVelocity;
                    compB = b.timeVelocity;
                    break;
                case 3:
                    compA = a.commitVelocity;
                    compB = b.commitVelocity;
                    break;
                case 4:
                    compA = a.comparison;
                    compB = b.comparison;
                    break;
                default:
                    compA = a.comparisonPercent;
                    compB = b.comparisonPercent;
            }
            if(compA > compB)
                return order;
            if(compA < compB)
                return -order;

            return 0;
        });
    }

    render() {
	    if(!this.props.project)
	        return null

        return (
	        <SelectableCardSummary type='students'
	                               values={ this.collect() }
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
        filtersHaveChanged: getFiltersHaveChanged(state),
        filters: getFilters(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentReportSummary)