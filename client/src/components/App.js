import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/App.css';
import Login from './Login'
import Main from './Main'
import { setToken } from '../redux/actions'

class App extends Component {

    loggedIn = () => {
        return this.props.token != null;
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
                    ? <Main />
                    : <Redirect to="/login" />
                }/>
            </Switch>
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
        setToken: (token) => dispatch(setToken(token)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
