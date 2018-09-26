import React, { Component } from 'react'
import { connect } from 'react-redux'

import logo from '../img/encourse-logo-large.png'
import { logIn } from '../redux/actions'
import url from '../server'

class Login extends Component {

    handleSubmit = (ev) => {
        ev.preventDefault()
        let form = new FormData()
        form.append('grant_type', 'password')
        form.append('username', ev.target.username.value)
        form.append('password', ev.target.password.value)
        form.append('client_id', 'encourse-client')

        let username = ev.target.username.value
        let password = ev.target.password.value
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
                        <p>Username</p>
                        <input type="text" name="username" />
                        <p>Password</p>
                        <input type="password" name="password" />
                        <br />
                        <input type="submit" value="Enter Credentials" />
                    </form>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return { 
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
    }
}
  
const mapDispatchToProps = (dispatch) => {
    return {
        logIn: (url, headers, body) => dispatch(logIn(url, headers, body))
    }
}
  
export default connect(mapStateToProps, mapDispatchToProps)(Login)