import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Checkbox, CheckmarkIcon, Summary, Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {history} from '../../../redux/store'
import {getStudentPreviews, setCurrentStudent} from '../../../redux/actions'
import PreviewCard from "../common/PreviewCard"

class ProjectTestSummary extends Component {

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
	}
}

const mapDispatchToProps = (dispatch) => {
	return {

	}
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectTestSummary)