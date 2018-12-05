import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Checkbox, CheckmarkIcon, Summary, Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {getCurrentProject, getTestScripts} from '../../../redux/state-peekers/projects'
import {retrieveTestScripts} from '../../../redux/retrievals/projects'
import PreviewCard from "../common/PreviewCard"

class ProjectTestSummary extends Component {

	componentDidMount() {
		if(this.props.project)
			retrieveTestScripts(this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveTestScripts(this.props.project)
	}
	
	render() {
		return (
			<Summary columns={ 5 }>
				{
				}
			</Summary>
		)
	}
}

const mapStateToProps = (state) => {
	return {
		project: getCurrentProject(state),
		testScripts: getTestScripts(state),
	}
}

export default connect(mapStateToProps)(ProjectTestSummary)