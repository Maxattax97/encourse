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
            visibility: '',
            filename: '',
            file: null
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
                    {
                        this.props.suites.length ?
                            <div>
                                <div className="h5 break-line"/>
                                <h4 className="header">
                                    Test Suite
                                </h4>
                                <input type="text" className="h3-size" value={this.state.interval} onChange={this.onChange} name="visibility" list="test-suites" />
                                <datalist id="test-suites">
                                    {
                                        this.props.suites.map(suite =>
                                            <option value={suite}/>
                                        )
                                    }
                                </datalist>
                            </div>
                            : null
                    }
                    <div className="h5 break-line"/>
					<h4 className="header">
						Visibility
					</h4>
                    <input type="text" className="h3-size" value={this.state.interval} onChange={this.onChange} name="visibility" list="visibility" />
                    <datalist id="visibility">
                        <option value="Visible" />
                        <option value="Hidden" />
                    </datalist>
                    <div className="h5 break-line"/>
					<h4 className='header'>
						Upload
					</h4>
                    <input type="text" className="h3-size" value={this.state.filename} onClick={ () => this.refs.fileUploader.click()}/>
                    <input type="file" style={{display:'none'}} ref="fileUploader" onChange={ (e) => console.log(e) }/>
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
	    suites: ['Part 1']
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectTestModal)