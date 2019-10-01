import React, { Component } from 'react'
import { connect } from 'react-redux'

import logo from '../resources/encourse-logo-large.png'
import { logIn, setLocation, authenticateToken, setTokens } from '../redux/actions'
import { LoadingIcon } from './Helpers'

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
            this.props.authenticateToken(`${url}/signin/${this.props.match.params.token}?uid=${this.props.match.params.uid}`)
            this.props.setTokens({uid: this.props.match.params.uid, token: this.props.match.params.token})
        }
    }

    render() {
        
        console.log(this.props.logInData)
        return (
            <div className="Login">
                <header className="Login-header">
                    <img src={logo} className="EnCourse-logo" alt="logo" />
                </header>
                {
                    this.props.logInData.signin === 2 &&
                    <div>
                        A sign in link has been sent to your email! Check your inbox.
                    </div>
                }
                {
                    this.props.logInData.signin === 0 &&
                    <div>
                        An error has occurred and the sign in link could not be sent. Please check the username again.
                    </div>
                }
                {this.props.logInIsLoading || this.props.authenticateTokenIsLoading  &&
                    (<div className='loading'>
                        <LoadingIcon/>
                    </div>)
                }
                <div className="credentials-container">
                    <form onSubmit={this.handleSubmit}>
                        <h3>Username</h3>
                        <input type="text" name="username" />
                        <input type="submit" value="Enter Credentials" />
                    </form>
                </div>


            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        logInIsLoading: state.auth ? state.auth.logInIsLoading : false,
        logInData: state.auth && state.auth.logInData ? state.auth.logInData : -1,
        authenticateTokenIsLoading: state.auth ? state.auth.authenticateTokenIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        authenticateToken: (url) => dispatch(authenticateToken(url)),
        logIn: (url) => dispatch(logIn(url)),
        setTokens: (token) => dispatch(setTokens(token)),
        setLocation: (location) => dispatch(setLocation(location)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Login)