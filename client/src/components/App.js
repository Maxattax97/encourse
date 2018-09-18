import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/App.css';
import Login from './Login'
import Main from './Main'

class App extends Component {

  loggedIn = () => {
    return true;
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
  return { }
}

const mapDispatchToProps = (dispatch) => {
	return { }
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
