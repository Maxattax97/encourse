import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"

class ProjectInfo extends Component {


	getProjectInfo = () => {
		const project = this.props.projects[this.props.current_project_index]
		if(!project)
			return []

		return [
			{
				stat_name: 'Project Name',
				stat_value: project.project_name
			},
			{
				stat_name: 'Repository Name',
				stat_value: project.source_name,
				break: true
			},
			{
				stat_name: 'Deadline',
				stat_value: project.due_date,
				break: true
			},
			{
				stat_name: 'Last Repository Sync',
				stat_value: project.last_sync
			},
			{
				stat_name: 'Last Ran Testall',
				stat_value: project.last_test
			}
		]
	}

	render() {
		return (
			<Statistics values={ this.getProjectInfo() }/>
		)
	}
}

const mapStateToProps = (state) => {
	return {
		projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
		current_project_index: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0
	}
}

export default connect(mapStateToProps, null)(ProjectInfo)
