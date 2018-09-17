import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/Main.css';
import Navbar from './Navbar'
import CoursePanel from './course/CoursePanel'
import StudentPanel from './StudentPanel'
import AdminPanel from './AdminPanel'
import UserSettings from './UserSettings'

class Main extends Component {

  render() {
    return (
        <div className="Main">
            <Navbar />
            <div className="Panel">
                <div className="Panel-Container">
                    <Switch>
                        <Route path="/panel" render={(navProps) => {
                            //determine logic for course panel, student panel, or admin panel. For now, use course panel
                            return <Redirect to="/course"/>
                        }}/>
                        <Route path="/course" render={(navProps) =>
                            <CoursePanel />
                        }/>
                        <Route path="/student" render={(navProps) =>
                            <StudentPanel />
                        }/>
                        <Route path="/admin" render={(navProps) =>
                            <AdminPanel />
                        }/>
                        <Route path="/settings" render={(navProps) =>
                            <UserSettings />
                        }/>
                        <Route path='/' render={(navProps) => <Redirect to="/panel" />}/>
                    </Switch>
                </div>
            </div>
           
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

export default connect(mapStateToProps, mapDispatchToProps)(Main)