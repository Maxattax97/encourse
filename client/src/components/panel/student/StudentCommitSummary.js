import React, { Component } from 'react'
import { connect } from 'react-redux'
import {getStudentPreviews, clearStudent, updateCommitsPage, resetCommitsPage} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {getCurrentCourseId, getCurrentSemesterId} from "../../../redux/state-peekers/course"
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveStudentCommitHistory} from '../../../redux/retrievals/student'
import {getCurrentStudent, getStudentCommitHistory} from '../../../redux/state-peekers/student'

class StudentCommitSummary extends Component {

    componentDidMount() {
        if(this.props.student && this.props.project) {
            this.props.resetCommitsPage()
            retrieveStudentCommitHistory(this.props.student, this.props.project, 1, 215)
        }
    }

    componentDidUpdate(prevProps) {
        if(this.props.student && this.props.project && (!(prevProps.project) || !(prevProps.student) || prevProps.project.index !== this.props.project.index)) {
            this.props.resetCommitsPage()
            retrieveStudentCommitHistory(this.props.student, this.props.project, 1, 215)
        }
    }

    clickCommitCard = (student) => {
    }

    renderPreview = (commit) => {

        return (
            <div>
            </div>
        )
    }

    render() {
        console.log(this.props.history)
        return (
            <SelectableCardSummary type='students'
                                   values={this.props.history.data}
                                   render={this.renderPreview}
                                   onClick={this.clickCommitCard} />
        )
    }
}


const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        history: getStudentCommitHistory(state),
        course: getCurrentCourseId(state),
        semester: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
        updateCommitsPage: () => dispatch(updateCommitsPage()),
        resetCommitsPage: () => dispatch(resetCommitsPage()),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentCommitSummary)