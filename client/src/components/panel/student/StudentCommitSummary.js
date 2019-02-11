import React, { Component } from 'react'
import { connect } from 'react-redux'
import {history} from '../../../redux/store'
import {getStudentPreviews, clearStudent, updateCommitsPage, resetCommitsPage} from '../../../redux/actions'
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {getCurrentCourseId, getCurrentSemesterId} from "../../../redux/state-peekers/course"
import SelectableCardSummary from "../common/SelectableCardSummary"
import {retrieveStudentCommitHistory} from '../../../redux/retrievals/student'
import {getCurrentStudent, getStudentCharts, getStudentCommitHistory} from '../../../redux/state-peekers/student'
import {Title} from '../../Helpers'
import {setCurrentCommit} from '../../../redux/actions/student'
import moment from 'moment'

class StudentCommitSummary extends Component {

    clickCommitCard = (commit) => {
        this.props.setCurrentCommit(commit)
        history.push(`/${this.props.course}/student/${this.props.student.studentID}/commit/${commit.hash}`)
    }

    renderPreview = (commit) => {

        return (
            <div>
                <Title>
                    <h4>{ commit.hash.substring(0, 7) }</h4>
                    <h4>{ moment(commit.date).format("M-D HH:mm:ss") }</h4>
                </Title>
                <div className="h4 break-line header" />
                <div className="preview-content">
                    <h5>Additions: { commit.additions }</h5>
                    <h5>Deletions: { commit.deletions }</h5>
                </div>
            </div>
        )
    }

    render() {
        return (
            <SelectableCardSummary type='students'
                                   values={(this.props.charts.data || {}).commits}
                                   render={this.renderPreview}
                                   onClick={this.clickCommitCard}
                                   noCheckmark />
        )
    }
}


const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        course: getCurrentCourseId(state),
        charts: getStudentCharts(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCurrentCommit: (commit) => dispatch(setCurrentCommit(commit)),
        updateCommitsPage: () => dispatch(updateCommitsPage()),
        resetCommitsPage: () => dispatch(resetCommitsPage()),
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentCommitSummary)