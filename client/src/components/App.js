import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../styles/css/login.css'
import Login from './Login'
import Main from './Main'

class App extends Component {

    loggedIn = () => {
        return this.props.token != null
    };

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
    }
}

export default connect(mapStateToProps, null)(App)
