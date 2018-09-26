import React, { Component } from 'react'
import { connect } from 'react-redux'

import logo from '../img/encourse-logo-large.png'
import { logIn } from '../redux/actions'
import url from '../server'

class Login extends Component {

    getBasicAuth = () => {
        let tok = 'encourse-client: encourse-password'
        let hash = btoa(tok);
        return 'Basic ' + hash;
    }

    handleSubmit = (ev) => {
        ev.preventDefault()
        let username = ev.target.username.value
        let password = ev.target.password.value
        this.props.logIn(`${url}/oauth/token`, {
            'Authorization': this.getBasicAuth()
        }, JSON.stringify({
            grant_type: 'password',
            username,
            password,
            client_id: 'encourse-client'
        }))
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