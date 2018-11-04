import React, { Component } from 'react'

import {LoadingIcon, Modal} from '../../Helpers'

class ShareReportModal extends Component {
	render() {
		return (
			<div className="course-modal">
				<Modal center id={this.props.id}>
					<h2 className='header'>Share Link</h2>
					<div className="h2 break-line header"/>
					{
						this.props.link ?
							<input type="text" className="h3-size" value={this.props.link} name="current_password" autoComplete="off"/>
							:
							<div className='loading'>
								<LoadingIcon/>
							</div>
					}
				</Modal>
			</div>
		)
	}
}

export default ShareReportModal
