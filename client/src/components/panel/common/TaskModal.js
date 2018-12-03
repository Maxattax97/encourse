import React, { Component } from 'react'
import { connect } from 'react-redux'

import {Modal} from '../../Helpers'

class CourseModal extends Component {

	render() {
		return (
			<div className="course-modal">
				<Modal center id={this.props.id} noExit>
                    <h2 className='header'>Current Task</h2>
                    <div className="h4 break-line header"/>
                    <h4 className='header'>
                        {
                            this.props.task.operation === 'sync' ? 'Sync' :
                                this.props.task.operation === 'test' ? 'Test' :
                                    this.props.task.operation === 'analyze' ? 'Analyze' :
                                        'None'
                        }
                    </h4>
					<div className="student-preview-progress">
						<div className="progress-bar">
							<div style={{width: (this.props.task.progress + '%')}} />
						</div>
						<h6 className="progress-text">
							{ this.props.task.estimated_time_remaining }
						</h6>
					</div>
				</Modal>
			</div>
		)
	}

}

const mapStateToProps = (state) => {
    return {
        task: { operation: 'sync', progress: 10, estimated_time_remaining: '1 day'}
    }
}

export default connect(mapStateToProps)(CourseModal)