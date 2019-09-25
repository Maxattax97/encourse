import React, { Component } from 'react'
import { connect } from 'react-redux'

import logo from '../resources/encourse-logo-large.png'
import { logIn, setLocation, authenticateToken } from '../redux/actions'

import url from '../server'

class Login extends Component {

    handleSubmit = (ev) => {
        ev.preventDefault()

        let username = ev.target.username.value

        this.props.logIn(`${url}/signin?username=${username}`)
    }

    componentDidMount = () => {
        this.props.setLocation(this.props.location.state ? this.props.location.prevRoute : null)
        if(this.props.match.params.uid && this.props.match.params.token) {
            this.props.authenticateToken(`${url}/signin/${this.props.match.params.token}`)
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
                        <h3>Username</h3>
                        <input type="text" name="username" />
                        <h3>Password</h3>
                        <input type="password" name="password" />
                        <input type="submit" value="Enter Credentials" />
                    </form>
                </div>
            </div>
        )
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        authenticateToken: (url) => dispatch(authenticateToken(url)),
        logIn: (url) => dispatch(logIn(url)),
        setLocation: (location) => dispatch(setLocation(location)),
    }
}

export default connect(null, mapDispatchToProps)(Login)