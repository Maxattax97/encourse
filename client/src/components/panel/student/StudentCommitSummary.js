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
import {getFilters, getFiltersHaveChanged} from '../../../redux/state-peekers/control'

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

    collect = () => {
        if(this.props.charts.isLoading || this.props.charts.error || !this.props.charts.data || !this.props.charts.data.commits || !this.props.charts.data.commits.map)
            return [];

        return this.props.charts.data.commits.sort((a, b) => {
            const order = this.props.filters.order_by === 0 ? 1 : -1;

            let compA;
            let compB;

            switch(this.props.filters.sort_by) {
                case 0:
                    return moment(a.date).diff(b.date) * order;
                case 1:
                    compA = a.additions;
                    compB = b.additions;
                    break;
                case 2:
                    compA = a.deletions;
                    compB = b.deletions;
                    break;
                default:
                    return moment(a.date).diff(b.date);
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
                                   values={this.collect()}
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
        charts: getStudentCharts(state),
        filtersHaveChanged: getFiltersHaveChanged(state),
        filters: getFilters(state)
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