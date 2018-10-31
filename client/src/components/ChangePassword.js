import React, { Component } from 'react'
import logo from '../resources/encourse-logo-large.png'

class ChangePassword extends Component {

    handleSubmit = (ev) => {
        ev.preventDefault()

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
                        <input type="password" name="password" />
                        <h3></h3>
                        <input type="submit" value="Change Password" />
                    </form>
                </div>
            </div>
        )
    }
}

export default ChangePassword