import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {retrieveCourseStats} from "../../../redux/retrievals/course"
import {getStats} from "../../../redux/state-peekers/course"
import {getCurrentProject} from "../../../redux/state-peekers/project"

class StudentStatistics extends Component {

	componentDidMount() {
		if(this.props.project)
			retrieveCourseStats(this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveCourseStats(this.props.project)
	}

	render() {
		return (
			<Statistics stats={this.props.stats} />
		)
	}
}

const mapStateToProps = (state) => {
	return {
		project: getCurrentProject(state),
		stats: getStats(state)
	}
}

export default connect(mapStateToProps, null)(StudentStatistics)
