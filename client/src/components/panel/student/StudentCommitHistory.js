import React, { Component } from 'react'
import { connect } from 'react-redux'

import { updateCommitsPage, resetCommitsPage } from '../../../redux/actions/index'
import CommitHistory from '../common/CommitHistory'
import {retrieveStudentCommitHistory} from "../../../redux/retrievals/student"
import {
	getCurrentStudent,
	getStudentCommitHistory,
	isLastCommitsPage
} from "../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../redux/state-peekers/project"

class StudentCommitHistory extends Component {

    componentWillMount() {
        if(this.props.student && this.props.project) {
	        this.props.resetCommitsPage()
	        retrieveStudentCommitHistory(this.props.student, this.props.project, 1, 5)
        }
    }

	componentWillReceiveProps(nextProps) {
		if(nextProps.student && nextProps.project && (!(this.props.project) || !(this.props.student) || this.props.project.index !== nextProps.project.index)) {
            this.props.resetCommitsPage()
			retrieveStudentCommitHistory(nextProps.student, nextProps.project, 1, 5)
        }
	}

	componentWillUnmount() {
	    this.props.resetCommitsPage()
    }

    scrolledToBottom = () => {
        if(!this.props.last && this.props.student && this.props.project) {
            retrieveStudentCommitHistory(this.props.student, this.props.project, this.props.page + 1, 5)
            this.props.updateCommitsPage()
        }
    }

    render() {
        return (
            <CommitHistory isLoading={ this.props.history.loading && !this.props.history.data.content } data={ this.props.history.data.content } onPaginate={ this.scrolledToBottom }/>
        )
    }
}

const mapStateToProps = (state) => {
    return {
	    student: getCurrentStudent(state),
	    project: getCurrentProject(state),
	    history: getStudentCommitHistory(state),
        page: state.student && state.student.commitsPage ? state.student.commitsPage : 1,
        last: isLastCommitsPage(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        updateCommitsPage: () => dispatch(updateCommitsPage()),
        resetCommitsPage: () => dispatch(resetCommitsPage()),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentCommitHistory)
