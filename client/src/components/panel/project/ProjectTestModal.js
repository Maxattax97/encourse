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
            suite: null,
            points: 5,
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
					<input type="text" className="h3-size" value={this.props.name} name="name" autoComplete="off"/>
                    <div>
                        <div className="h5 break-line"/>
                        <h4 className="header">
                            Test Suite
                        </h4>
                        <input type="text" className="h3-size" value={this.state.suite} onChange={this.onChange} name="suite" list="test-suites" />
                        <datalist id="test-suites">
                            {
                                this.props.suites.map(suite =>
                                    <option value={suite}/>
                                )
                            }
                        </datalist>
                    </div>
                    <div className="h5 break-line"/>
                    <h4 className="header">
                        Point Value
                    </h4>
                    <input type="number" className="h3-size" value={this.state.points} onChange={this.onChange} name="points" ref="interval"/>
                    <div className="h5 break-line"/>
					<h4 className="header">
						Visibility
					</h4>
                    <input type="text" className="h3-size" value={this.state.visibility} onChange={this.onChange} name="visibility" list="visibility" />
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