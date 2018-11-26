import React, { Component } from 'react'
import { connect } from 'react-redux'

import {setDirectory, modifyProject} from '../../../redux/actions/index'
import url from '../../../server'
import {CheckmarkIcon, Modal} from '../../Helpers'

class CourseModal extends Component {

	render() {
		return (
			<div className="course-modal">
				<Modal center id={this.props.id} noExit>
					<h4 className='header'>{this.props.header}</h4>
					<div className="student-preview-progress">
						<div className="progress-bar">
							<div style={{width: (this.props.progress + '%')}} />
						</div>
						<h6 className="progress-text">
							{ parseInt(this.props.progress) }%
						</h6>
					</div>
				</Modal>
			</div>
		)
	}

}

export default connect(null, null)(CourseModal)