import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import { clearStudent} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {
    getCurrentCourseId
} from '../../../redux/state-peekers/course'
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveStudentComparisons} from '../../../redux/retrievals/student'
import {getCurrentStudent, getStudentComparisons} from '../../../redux/state-peekers/student'

class StudentComparisonSummary extends Component {

    componentDidMount() {
        if(this.props.project && this.props.student)
            retrieveStudentComparisons(this.props.project, this.props.student)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project !== prevProps.project || this.props.student !== prevProps.student)
            retrieveStudentComparisons(this.props.project, this.props.student)
    }

    clickStudentCard = (student) => {
        //this.props.clearStudent()
        //history.push(`/${this.props.course}/student-dishonesty/${student.studentID}`)
    }

    renderPreview = (student) => {
        return (
            <div>
                <Title>
                    <h4>{ student.lastName }</h4>
                </Title>
                <div className="h4 break-line header" />
                <div className="preview-content">
                    <h5>Total: { student.count }</h5>
                    <h5>Percent: { student.percent.toFixed(2) }</h5>
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
        students: getStudentComparisons(state),
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        course: getCurrentCourseId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        clearStudent: () => dispatch(clearStudent),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentComparisonSummary)