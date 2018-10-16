import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'

import '../styles/css/base.css'
import '../styles/css/main.css'
import Navbar from './navigation/TopNavigation'
import CoursePanel from './panels/CoursePanel'
import ProjectModal from './modals/ProjectModal'
import StudentPanel from './panels/StudentPanel'
import AdminPanel from './panels/AdminPanel'
import PreferencePanel from './panels/PreferencePanel'
import ProjectPanel from './panels/ProjectPanel'

class Main extends Component {

    render() {
        return (
            <div className="main">
                <Navbar />
                <div className="panel">
                    <div className="panel-container">
                        <Switch>
                            <Route path="/panel" render={(/* navProps */) => {
                                //determine logic for course panel, student panel, or admin panel. For now, use course panel
                                return <Redirect to="/cs252/course"/>
                            }}/>
                            <Route path="/:courseid/course" render={(navProps) =>
                                <CoursePanel {...navProps} />
                            }/>
                            <Route path="/:courseid/student/:id" render={(navProps) =>
                                <StudentPanel {...navProps} />
                            }/>
                            <Route path="/admin" render={(navProps) =>
                                <AdminPanel {...navProps} />
                            }/>
                            <Route path="/:courseid/projects" render={(navProps) =>
                                <ProjectPanel {...navProps} />
                            } />
                            <Route path="/settings" render={(/* navProps */) =>
                                <PreferencePanel />
                            }/>
                            <Route path='/' render={(/* navProps */) => <Redirect to="/panel" />}/>
                        </Switch>
                    </div>
                </div>

            </div>
        )
    }
}

const mapStateToProps = (/* state */) => {
    return { }
}

const mapDispatchToProps = (/* dispatch */) => {
    return { }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))