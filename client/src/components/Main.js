import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'

import '../css/Main.css';
import Navbar from './Navbar'
import CoursePanel from './course/CoursePanel'
import ProjectOptions from "./project/ProjectOptions"
import StudentPanel from './student/StudentPanel'
import AdminPanel from './AdminPanel'
import Preferences from './Preferences'

class Main extends Component {

  render() {
    return (
        <div className="main">
            <Navbar />
            <div className="panel">
                <div className="panel-container">
                    <Switch>
                        <Route path="/panel" render={(navProps) => {
                            //determine logic for course panel, student panel, or admin panel. For now, use course panel
                            return <Redirect to="/course"/>
                        }}/>
                        <Route path="/project-settings" render={(navProps) =>
                            <ProjectOptions {...navProps} />
                        } />
                        <Route path="/course" render={(navProps) =>
                            <CoursePanel {...navProps} />
                        }/>
                        <Route path="/student" render={(navProps) =>
                            <StudentPanel />
                        }/>
                        <Route path="/admin" render={(navProps) =>
                            <AdminPanel />
                        }/>
                        <Route path="/settings" render={(navProps) =>
                            <Preferences />
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

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))