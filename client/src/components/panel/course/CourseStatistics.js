import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {retrieveCourseStats} from "../../../redux/retrievals/course"
import {getStats} from "../../../redux/state-peekers/course"
import {getCurrentProject} from "../../../redux/state-peekers/project"

class StudentStatistics extends Component {

	componentWillMount() {
		if(this.props.project)
			retrieveCourseStats(this.props.project)
	}

	componentWillReceiveProps(nextProps) {
		if(nextProps.project && (!(this.props.project) || this.props.project.index !== nextProps.project.index))
			retrieveCourseStats(nextProps.project)
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
