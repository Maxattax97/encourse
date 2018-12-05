import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {
    toggleSelectCard
} from '../../../redux/actions'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {retrieveAllStudents} from '../../../redux/retrievals/course'
import {getCurrentProject} from '../../../redux/state-peekers/projects'
import {getStudents, getCurrentCourseId, getCurrentSemesterId, getSections} from '../../../redux/state-peekers/course'
import {getAllSelected, getSelected, isAnySelected} from '../../../redux/state-peekers/control'

class StudentAssignSummary extends Component {

    componentDidMount() {
        if(this.props.project)
            retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
    }
	renderPreview = (student) => {

	    return (
	        <div>
                <Title>
                    <h4>{ student.first_name }</h4>
                    <h4>{ student.last_name }</h4>
                </Title>
                <div className="h4 break-line header" />
                <div className="preview-content">
                    <h5>Sections : { this.props.sections.loading ? 'Loading' : student.sections.map((id, index) => (this.props.sections.data.find(section => section.id === id).name) + (index === student.sections.length - 1 ? '' : ', ')) }</h5>
                    <h5>TAs : { student.teaching_assistants.map((id, index) => (id + (index === student.teaching_assistants.length - 1 ? '' : ', '))) }</h5>
                </div>
	        </div>
	    )
	}

    isSelected = (value) => {
        return this.props.sectionsSelectedAll || (this.props.sectionsSelected(value))
    }

	render() {
	    if(!this.props.project || !this.props.students.data)
	        return null

	    return (
	        <SelectableCardSummary type='students'
			                       values={this.props.students.data.filter(student => student.sections.some(section => this.isSelected(section)))}
			                       render={this.renderPreview}
                                   onClick={(value) => this.props.toggleSelectCard('students', value.id)}
                                   noReset/>
	    )
	}
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        sections: getSections(state),
        project: getCurrentProject(state),
        course: getCurrentCourseId(state),
        semester: getCurrentSemesterId(state),
        sectionsSelectedAll: getAllSelected(state, 'sections'),
        sectionsSelected: getSelected(state, 'sections'),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleSelectCard: (id, index) => dispatch(toggleSelectCard(id, index)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentAssignSummary)