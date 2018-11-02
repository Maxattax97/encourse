import React, { Component } from 'react'
import logo from '../resources/encourse-logo-large.png'

import { changePassword } from '../redux/actions'

class ChangePassword extends Component {

    handleSubmit = (ev) => {
        ev.preventDefault()
        const old = ev.target.value.current_password
        const password = ev.target.value.password
        const confirm = ev.target.value.confirm_password
        if(password === confirm) {
            
        } else {
            //TODO: update UI
            alert('Passwords do not match!')
        }
    }


    render() {
        return (
            <div className="Login">
                <header className="Login-header">
                    <img src={logo} className="EnCourse-logo" alt="logo" />
                </header>
                <div className="credentials-container">
                    <form onSubmit={this.handleSubmit}>
                        <h3>Current Password</h3>
                        <input type="password" name="current_password" />
                        <h3>New Password</h3>
                        <input type="password" name="password" />
                        <h3>Confirm Password</h3>
                        <input type="password" name="confirm_password" />
                        <h3></h3>
                        <input type="submit" value="Change Password" />
                    </form>
                </div>
            </div>
        )
    }
}

export default ChangePassword