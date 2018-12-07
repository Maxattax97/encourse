import React, { Component } from 'react'
import { connect } from 'react-redux'
import {parseDiff, Diff} from 'react-diff-view';
import {
	getCommitSource,
	getCurrentCommit,
	getCurrentStudent,
	getStudentStatistics
} from "../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../redux/state-peekers/projects"
import {retrieveSource} from "../../../redux/retrievals/student"

class StudentCommitDiff extends Component {

	componentDidMount() {
		if(this.props.student && this.props.project && this.props.commit)
			retrieveSource(this.props.student, this.props.project, this.props.commit.hash)
	}

	render() {
		if(!this.props.source.data.contents)
			return null

		const files = parseDiff(this.props.source.data.contents);
		return (
			<div>
				{files.map(({hunks}, i) => <Diff key={i} hunks={hunks} viewType="split" />)}
			</div>
		)
	}
}

const mapStateToProps = (state) => {
	return {
		student: getCurrentStudent(state),
		project: getCurrentProject(state),
		commit: getCurrentCommit(state),
		source: getCommitSource(state)
	}
}

export default connect(mapStateToProps)(StudentCommitDiff)
