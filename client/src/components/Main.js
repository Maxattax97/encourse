import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'

import '../styles/css/base.css'
import '../styles/css/main.css'
import Navbar from './navigation/TopNavigation'
import CoursePanel from './panel/CoursePanel'
import ProjectModal from './modal/ProjectModal'
import StudentPanel from './panel/StudentPanel'
import AdminPanel from './panel/AdminPanel'
import PreferencePanel from './panel/PreferencePanel'
import ProjectPanel from './panel/ProjectPanel'

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
                            <Route path="/projects" render={(navProps) =>
                                <ProjectPanel {...navProps} />
                            }/>
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