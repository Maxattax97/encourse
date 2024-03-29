import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {getCurrentProject} from "../../../redux/state-peekers/projects"

class ProjectInfo extends Component {


	getProjectInfo = () => {
		if(!this.props.project)
			return { loading: true, data: [] }

		return {
			data: [	
			{
				stat_name: 'Project Name',
				stat_value: this.props.project.project_name
			},
			{
				stat_name: 'Repository Name',
				stat_value: this.props.project.source_name,
				break: true
			},
			{
				stat_name: 'Deadline',
				stat_value: this.props.project.due_date,
				break: true
			},
			{
				stat_name: 'Last Repository Sync',
				stat_value: this.props.project.last_sync
			},
			{
				stat_name: 'Last Ran Testall',
				stat_value: this.props.project.last_test
			}
		] }
	}

	render() {
		return (
			<Statistics stats={ this.getProjectInfo() }/>
		)
	}
}

const mapStateToProps = (state) => {
	return {
		project: getCurrentProject(state)
	}
}

export default connect(mapStateToProps, null)(ProjectInfo)
