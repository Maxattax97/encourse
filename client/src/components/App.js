import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../styles/css/login.css'
import Login from './Login'
import Main from './Main'
import { setToken, logOutClient } from '../redux/actions'

class App extends Component {

    loggedIn = () => {
        return this.props.token != null
    };

    componentWillReceiveProps(nextProps) {
        if(nextProps.logOutHasError) {
            nextProps.logOut()
        }
    }

    componentDidMount = () => {
        if(localStorage.getItem('token') != null) {
            let token = JSON.parse(localStorage.getItem('token'))
            this.props.setToken(token)
        }
    }

    render() {
        return (
            <div className="App">
                <Switch>
                    <Route path="/login" render={(navProps) =>
                        !this.loggedIn()
                            ? <Login {...navProps} />
                            : <Redirect to="/course"/>
                    }/>
                    <Route path="/" render={(navProps) =>
                        this.loggedIn()
                            ? <Main {...navProps} />
                            : <Redirect to="/login" />
                    }/>
                </Switch>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        logOutHasError: state.auth ? state.auth.logOutHasError : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setToken: (token) => dispatch(setToken(token)),
        logOut: () => dispatch(logOutClient())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
