import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { defaultCourse, defaultSemester } from '../defaults'
import { history } from '../redux/store'
import { setModalState, getAccount, setCurrentCourse, setCurrentSemester } from '../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from '../redux/state-peekers/course'
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
        const course = this.props.match.params.courseID ? this.props.match.params.courseID : defaultCourse
        const semester = this.props.match.params.semesterID ? this.props.match.params.semesterID : defaultSemester
        const page = this.props.path.substring(this.props.path.lastIndexOf('/') + 1)

        if(/^((Fall)|(Spring)|(Summer))2[0-9][0-9][0-9]$/.test(semester)) {
            this.props.setCurrentSemester(semester)
        } 

        if(/^[a-z]+[0-9]{3,}$/.test(course)) {
            this.props.setCurrentCourse(course)
        } 

        history.push(`/${course}/${semester}/${page}`)
    
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
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
        path: state.router && state.router.location ? state.router.location.pathname : null, 
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        closeModal: () => dispatch(setModalState(0)),
        getAccount: (url, headers, body) => dispatch(getAccount(url, headers, body)),
        setCurrentCourse: (id) => dispatch(setCurrentCourse(id)),
        setCurrentSemester: (id) => dispatch(setCurrentSemester(id)),
    }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))