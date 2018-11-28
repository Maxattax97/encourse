import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { history } from '../redux/store'
import { defaultCourse } from '../defaults'

import '../styles/css/login.css'
import Login from './Login'
import Main from './Main'

class App extends Component {

    constructor(props) {
        super(props)

        this.state = {
            prevRoute: ''
        }
    }

    loggedIn = () => {
        return this.props.token != null
    }

    updateState = () => {
        return this.setState({ prevRoute: history.location.pathname})
    }

    render() {
        return (
            <div className="App">
                <Switch>
                    <Route path="/login" render={(navProps) => {
                        console.log(this.props.prevRoute)
                        return !this.loggedIn()
                            ? <Login {...navProps} />
                            : <Redirect to={`/course/${defaultCourse}`}/>
                    }

                    }/>
                    <Route path="/" onChange={this.updateState} render={(navProps) => {
                        return this.loggedIn()
                            ? <Main {...navProps} />
                            : <Redirect to={{
                                pathname: '/login',
                                state: { prevRoute: history.location.pathname }
                            }} />
                    }
                       
                    }/>
                </Switch>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        prevRoute: state.auth ? state.auth.location : null
    }
}

export default connect(mapStateToProps, null)(App)
