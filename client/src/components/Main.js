import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'

import '../css/Main.css'
import Navbar from './navigation/TopNavigation'
import CoursePanel from './panels/CoursePanel'
import ProjectModal from './modals/ProjectModal'
import StudentPanel from './panels/StudentPanel'
import AdminPanel from './panels/AdminPanel'
import PreferencePanel from './panels/PreferencePanel'

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
                                <ProjectModal {...navProps} />
                            } />
                            <Route path="/course" render={(navProps) =>
                                <CoursePanel {...navProps} />
                            }/>
                            <Route path="/student/:id" render={(navProps) =>
                                <StudentPanel {...navProps} />
                            }/>
                            <Route path="/admin" render={(navProps) =>
                                <AdminPanel {...navProps} />
                            }/>
                            <Route path="/settings" render={(navProps) => 
                                <PreferencePanel />   
                            }/>
                            <Route path='/' render={(navProps) => <Redirect to="/panel" />}/>
                        </Switch>
                    </div>
                </div>
           
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return { }
}

const mapDispatchToProps = (dispatch) => {
    return { }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))