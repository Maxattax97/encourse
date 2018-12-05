import React, { Component } from 'react'
import { connect } from 'react-redux'
import Calendar from 'rc-calendar';
import 'rc-calendar/assets/index.css';

import {CheckmarkIcon, Modal} from '../../Helpers'
import moment from "moment"

class ProjectModal extends Component {

	due_date_temp = ''
	due_date_temp_time = moment()

	constructor(props) {
		super(props)

		this.state = {
			name: '',
			source_name: '',
			due_date: moment(),
			date_picker: false
		}
	}

	onChange = (event) => {
		this.setState({ [event.target.name]: event.target.value })
	};

	saveSettings = () => {
		if(this.state.date_picker) {
			this.setState({ date_picker: false, due_date: this.due_date_temp })
		}
		else {

		}
	};

	clickDeadline = () => {
		this.setState({ date_picker: true })
		this.due_date_temp = this.state.due_date
	}

	selectDate = (current) => {
		if(this.due_date_temp.diff(current, 'days') === 0 && moment().diff(this.due_date_temp_time, 'milliseconds') < 300) {
			this.saveSettings()
		}
		else {
			this.due_date_temp = current
			this.due_date_temp_time = moment()
		}
	}

	openModal = () => {
		this.due_date_temp = this.state.due_date
		this.setState({ date_picker: false})
		return true
	}

	closeModal = () => {
		if(this.state.date_picker) {
			this.setState({ date_picker: false })
			return false
		}
		return true
	}

	render() {
		return (
			<div className="course-modal">
				<Modal center id={this.props.id} onClose={ this.closeModal } onOpen={ this.openModal }>
					{
						this.state.date_picker ?
							<div>
								<h4 className='header'>Deadline</h4>
								<Calendar showToday={false}
								          showDateInput={false}
								          value={this.due_date_temp}
								          disabledDate={ (current) => current.diff(moment(), 'days') < 0}
								          onSelect={ this.selectDate }/>
							</div>
							: <div>
								<h2 className='header'>{ this.props.newProject ? 'New Project' : 'Project Settings' }</h2>
								<div className="h2 break-line header"/>
								<h4 className="header">
									Name
								</h4>
								<input type="text" className="h3-size" value={this.props.courseID} name="name" autoComplete="off"/>
								<h4 className="header">
									Repository
								</h4>
								<input type="number" className="h3-size" value={this.state.source_name} onChange={this.onChange} name="source_name" ref="interval"/>
								<h4 className='header'>
									Deadline
								</h4>
								<input type="text" className="h3-size" value={this.state.due_date.format('DD-MM-YYYY')} name="due_date" onClick={ this.clickDeadline } autoComplete="off"/>
							</div>
					}
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

export default connect(mapStateToProps, mapDispatchToProps)(ProjectModal)