import React, { Component } from 'react'
import { connect } from 'react-redux'
import url from '../../../server'
import { defaultCourse } from '../../../defaults'
import {CheckmarkIcon, Modal} from '../../Helpers'

class ProjectTestModal extends Component {

	constructor(props) {
		super(props)

		this.state = {
			name: '',
			source_name: '',
			created_date: '',
			due_date: '',
		}
	}

	onChange = (event) => {
		this.setState({ [event.target.name]: event.target.value })
	};

	saveSettings = () => {
		if(this.state.name === defaultCourse) {
			this.props.setDirectory(`${url}`)
		}
		for(let project of this.props.projects) {
			// console.log(project)
			this.props.modifyProject(`${url}/api/modify/project?projectID=${project.id}&field=testRate&value=${this.state.interval}`)
		}
	};

	render() {
		return (
			<div className="course-modal">
				<Modal center id={this.props.id}>
					<h2 className='header'>{ this.props.newTestScript ? 'New Test Script' : 'Test Script Settings' }</h2>
					<div className="h2 break-line header"/>
					<h4 className="header">
						Name
					</h4>
					<input type="text" className="h3-size" value={this.props.courseID} name="name" autoComplete="off"/>
					<h4 className="header">
						Visibility
					</h4>
					<input type="number" className="h3-size" value={this.state.interval} onChange={this.onChange} name="interval" ref="interval"/>
					<h4 className='header'>
						Upload
					</h4>
					<div className="modal-buttons float-height">
						<div className="svg-icon action" onClick={ this.saveSettings }>
							<CheckmarkIcon/>
						</div>
					</div>
				</Modal>
			</div>
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

export default connect(mapStateToProps, mapDispatchToProps)(ProjectTestModal)