import React, { Component } from 'react'
import { connect } from 'react-redux'

import {CheckmarkIcon, Modal} from '../../Helpers'

class CourseModal extends Component {

    constructor(props) {
        super(props)

        this.state = {
            current_password: '',
            new_password: '',
            new_password_confirm: ''
        }
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
    };

    saveSettings = () => {

    }

    render() {
        return (
            <div className="course-modal">
                <Modal center id={this.props.id}>
                    <h2 className='header'>Change Password</h2>
                    <div className="h2 break-line header"/>
                    <h4 className="header">
                        Current Password
                    </h4>
                    <input type="password" className="h3-size" onChange={ this.onChange } value={this.state.current_password} name="current_password" autoComplete="off"/>
                    <h4 className="header">
                        New Password
                    </h4>
                    <input type="password" className="h3-size" onChange={ this.onChange } value={this.state.new_password} name="new_password" autoComplete="off"/>
                    <h4 className="header">
                        Confirm Password
                    </h4>
                    <input type="password" className="h3-size" onChange={ this.onChange } value={this.state.new_password_confirm} name="new_password_confirm" autoComplete="off"/>
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

const mapDispatchToProps = (dispatch) => {
    return {

    }
}

export default connect(null, mapDispatchToProps)(CourseModal)