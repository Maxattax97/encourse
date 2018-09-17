import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../App.css';

class App extends Component {

  loggedIn = () => {
    return true;
  }

  render() {
    return (
      <div className="App">
     
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
