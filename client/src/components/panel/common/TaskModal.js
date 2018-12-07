import React, { Component } from 'react'
import { connect } from 'react-redux'

import {Modal} from '../../Helpers'
import {getCurrentProject, getOperation} from '../../../redux/state-peekers/projects'
import {retrieveOperation} from '../../../redux/retrievals/projects'

class CourseModal extends Component {

    requestInterval

    requestFunc = () => {
        if(this.props.project && this.props.id === this.props.modalState) {
            retrieveOperation(this.props.project)
        }
    }

    componentDidMount() {
        this.requestFunc()
        this.requestInterval = setInterval(this.requestFunc, 5000)
    }

    componentWillUnmount() {
        clearInterval(this.requestInterval)
    }

	render() {
	    if(!this.props.project)
	        return (
	            <div className='course-modal'>
                    <Modal center id={this.props.id} noExit>
                        <h3>
                            No current project
                        </h3>
                    </Modal>
                </div>
            )

		return (
			<div className="course-modal">
				<Modal center id={this.props.id} noExit>
                    <h4 className='header'>
                        Last Sync
                    </h4>
                    <div>
                        <h5>
                            { this.props.project.last_sync}
                        </h5>
                    </div>
                    <div className="h5 break-line"/>
                    <h4 className='header'>
                        Last Testall
                    </h4>
                    <div>
                        <h5>
                            { this.props.project.last_test}
                        </h5>
                    </div>
                    <div className="h5 break-line"/>
                    <h4 className='header'>Current Task</h4>
                    <div>
                        <h5 className='header'>
                            {
                                this.props.task.operation === 'sync' ? 'Sync' :
                                    this.props.task.operation === 'test' ? 'Test' :
                                        this.props.task.operation === 'analyze' ? 'Analyze' :
                                            'None'
                            }
                        </h5>
                    </div>
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
        modalState: state.control && state.control.modalState ? state.control.modalState : 0,
        task: getOperation(state).data,
        project: getCurrentProject(state)
    }
}

export default connect(mapStateToProps)(CourseModal)