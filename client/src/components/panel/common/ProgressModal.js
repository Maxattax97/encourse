import React, { Component } from 'react'
import { connect } from 'react-redux'

import {setDirectory, modifyProject} from '../../../redux/actions/index'
import url from '../../../server'
import {CheckmarkIcon, Modal} from '../../Helpers'

class CourseModal extends Component {

	render() {
		return (
			<div className="course-modal">
				<Modal center id={this.props.id}>
					<h4 className='header'>{ this.props.header} </h4>

				</Modal>
			</div>
		)
	}

}

export default connect(null, null)(CourseModal)