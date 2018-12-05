import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { defaultCourse, defaultSemester } from '../defaults'

import { setModalState, getAccount } from '../redux/actions'
import url from '../server'

import '../styles/css/base.css'
import '../styles/css/main.css'
import Navbar from './navigation/TopNavigation'
import {AdminPanel, CoursePanel, CourseDishonestyPanel, ManageTAPanel, PreferencePanel, ProjectPanel, StudentDishonestyPanel, StudentPanel} from "./panel"

class Main extends Component {

    createPanelRef = (node) => {

        if(this.panel)
            this.panel.removeEventListener('scroll', this.scrollPanel)

        this.panel = node

        if(this.panel)
            this.panel.addEventListener('scroll', this.scrollPanel)
    }

    scrollPanel = (e) => {
        if(e.currentTarget.scrollHeight - e.currentTarget.scrollTop <= e.currentTarget.clientHeight+1 && this.currentChild && this.currentChild.scrolledToBottom)
            this.currentChild.scrolledToBottom()
    }

    componentDidMount = () => {
        if(!this.props.account) {
            this.props.getAccount(`${url}/api/account`)
        }
    }

    componentWillUnmount() {
        if(this.panel)
            this.panel.removeEventListener('scroll', this.scrollPanel)
    }

    setChild = (node) => {
        if(node)
            this.currentChild = node.getWrappedInstance()
    }

    render() {
        return (
            <div className="main">
                <Navbar />
                <div className="panel" ref={ this.createPanelRef }>
                    <div className="panel-container">
                        <div className={'modal-overlay' + ( this.props.isModalFocused ? ' show' : '' )}
                            style={ this.props.isModalFocused ? { } : { 'display': 'none' } }
                            onClick={ this.props.closeModal }
                        />
                        <Switch>
                            <Route path="/:courseID/:semesterID/course" render={(navProps) =>
                                <CoursePanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/:semesterID/student/:id" render={(navProps) =>
                                <StudentPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/:semesterID/admin" render={(navProps) =>
                                <AdminPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/:semesterID/projects" render={(navProps) =>
                                <ProjectPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/:semesterID/manage-tas" render={(navProps) =>
                                <ManageTAPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/:semesterID/course-dishonesty" render={(navProps) =>
                                <CourseDishonestyPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/:semesterID/student-dishonesty/:id" render={(navProps) =>
                                <StudentDishonestyPanel ref={ this.setChild } {...navProps}/>
                            }/>
                            <Route path="/:courseID/:semesterID/settings" render={(/* navProps */) =>
                                <PreferencePanel ref={ this.setChild } />
                            }/>
                            <Route path='/' render={(/* navProps */) => <Redirect to={`/${defaultCourse}/${defaultSemester}/course`}/>}/>
                        </Switch>
                    </div>
                </div>

            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        isModalFocused: !!(state.control && state.control.modalState),
        account: state.auth && state.auth.getAccountData ? state.auth.getAccountData : null,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        closeModal: () => dispatch(setModalState(0)),
        getAccount: (url, headers, body) => dispatch(getAccount(url, headers, body)),
    }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))