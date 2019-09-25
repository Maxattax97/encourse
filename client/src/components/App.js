import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import '../styles/css/login.css'
import { getCurrentCourseId, getCurrentSemesterId } from '../redux/state-peekers/course'
import { history } from '../redux/store'
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

    render() {
        return (
            <div className="App">
                <Switch>
                    <Route exact path="/signin" render={(navProps) => {
                        return !this.loggedIn()
                            ? <Login {...navProps} />
                            : <Redirect to={`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`}/>
                    }}
                    />
                    {/*TODO: Add custom logging in page*/}
                    <Route path="/signin/:uid/:token" render={navProps => {
                        return !this.loggedIn()
                            ? <Login {...navProps} />
                            : <Redirect to={`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`}/>
                    }}
                    />
                    <Route path="/:courseID/:semesterID" render={(navProps) => {
                        return this.loggedIn()
                            ? <Main {...navProps} />
                            : <Redirect to={{
                                pathname: '/signin',
                                state: { prevRoute: history.location.pathname }
                            }} />
                        }}
                    />
                    
                    <Route path="/" render={(navProps) => 
                        !this.loggedIn()
                        ? <Redirect to="/signin" />
                        : <Redirect to={`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`}/>
                    }
                     />
                    
                       
                    }/>
                </Switch>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.authenticateTokenData ? state.auth.logInData.token : null,
        prevRoute: state.auth ? state.auth.location : null,
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {

    }
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
