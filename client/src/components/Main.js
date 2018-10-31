import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'

import { setModalState } from '../redux/actions'

import '../styles/css/base.css'
import '../styles/css/main.css'
import Navbar from './navigation/TopNavigation'
import CoursePanel from './panel/CoursePanel'
import StudentPanel from './panel/StudentPanel'
import AdminPanel from './panel/AdminPanel'
import PreferencePanel from './panel/PreferencePanel'
import ProjectPanel from './panel/ProjectPanel'
import ManageTAPanel from './panel/ManageTAPanel'
import CourseDishonestyPanel from './panel/CourseDishonestyPanel'
import StudentDishonestyPanel from './panel/StudentDishonestyPanel'

class Main extends Component {

    render() {
        return (
            <div className="main">
                <Navbar />
                <div className="panel">
                    <div className="panel-container">
                        <div className={'modal-overlay' + ( this.props.isModalFocused ? ' show' : '' )}
                            style={ this.props.isModalFocused ? { } : { 'display': 'none' } }
                            onClick={ this.props.closeModal }
                        />
                        <Switch>
                            <Route path="/panel" render={(/* navProps */) => {
                                //determine logic for course panel, student panel, or admin panel. For now, use course panel
                                return <Redirect to="/course"/>
                            }}/>
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
                            <Route path="/manage-tas" render={(navProps) =>
                                <ManageTAPanel {...navProps} />
                            }/>
                            <Route path="/course-dishonesty" render={(navProps) =>
                                <CourseDishonestyPanel {...navProps} />
                            }/>
                            <Route path="/student-dishonesty/:id" render={(navProps) =>
                                <StudentDishonestyPanel {...navProps}/>
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

const mapStateToProps = (state) => {
    return {
        isModalFocused: state.modal && state.modal.isModalFocused ? state.modal.isModalFocused : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        closeModal: () => dispatch(setModalState(0)),
    }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))