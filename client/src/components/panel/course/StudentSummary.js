import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import {
    setCurrentStudent
} from '../../../redux/actions'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {retrieveAllStudents} from '../../../redux/retrievals/course'
import {getCurrentProject} from '../../../redux/state-peekers/projects'
import {getStudents, getCurrentCourseId, getCurrentSemesterId} from '../../../redux/state-peekers/course'
import {getFilters} from '../../../redux/state-peekers/control'

class StudentSummary extends Component {

    componentDidMount() {
        if(this.props.project)
            retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
    }

	clickStudentCard = (student) => {
	    this.props.setCurrentStudent(student)
	    history.push(`/${this.props.course}/${this.props.semester}/student/${student.id}`)
	};

	renderPreview = (student) => {
	    const hasVisible = !this.props.filters.view_filter || this.props.filters.view_filter === 1
        const hasHidden = !this.props.filters.view_filter || this.props.filters.view_filter === 2
	    const totalPoints = (hasVisible ? student.totals[this.props.project.id] : 0) + (hasHidden ? student.hiddenTotals[this.props.project.id] : 0)
        const points = (hasVisible ? student.points[this.props.project.id] : 0) + (hasHidden ? student.hiddenPoints[this.props.project.id] : 0)

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
	                    <div style={{width: (totalPoints <= 0.01 ? 100 : points / totalPoints * 100.0) + '%'}} />
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
			                       onClick={this.clickStudentCard} />
	    )
	}
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        project: getCurrentProject(state),
        course: getCurrentCourseId(state),
        semester: getCurrentSemesterId(state),
        filters: getFilters(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentSummary)