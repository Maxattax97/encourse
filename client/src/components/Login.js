import React, { Component } from 'react'
import { connect } from 'react-redux'

import logo from '../resources/encourse-logo-large.png'
import { logIn } from '../redux/actions'
import url from '../server'

class Login extends Component {

    handleSubmit = (ev) => {
        ev.preventDefault()

        let username = ev.target.username.value
        let password = ev.target.password.value

        let form = new FormData()
        form.append('grant_type', 'password')
        form.append('username', username)
        form.append('password', password)
        form.append('client_id', 'encourse-client')

        this.props.logIn(`${url}/oauth/token`, {
            'Authorization': `Basic ${btoa('encourse-client:encourse-password')}`,
        }, form)
    }

    render() {
        return (
            <div className="Login">
                <header className="Login-header">
                    <img src={logo} className="EnCourse-logo" alt="logo" />
                </header>
                <div className="credentials-container">
                    <form onSubmit={this.handleSubmit}>
                        <h3>Username</h3>
                        <input type="text" name="username" />
                        <h3>Password</h3>
                        <input type="password" name="password" />
                        <h3></h3>
                        <input type="submit" value="Enter Credentials" />
                    </form>
                </div>
            </div>
        )
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        logIn: (url, headers, body) => dispatch(logIn(url, headers, body))
    }
}

export default connect(null, mapDispatchToProps)(Login)