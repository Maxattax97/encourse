import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { defaultCourse } from '../defaults'
import { history } from '../redux/store'
import { setModalState, getAccount, setCurrentCourse, setCurrentSemester, clearCurrentCourse, clearCurrentSemester } from '../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from '../redux/state-peekers/course'
import url from '../server'
import '../styles/css/base.css'
import '../styles/css/main.css'
import Navbar from './navigation/TopNavigation'
import {AdminPanel, CoursePanel, CourseDishonestyPanel, ManageTAPanel, PreferencePanel, ProjectPanel, StudentDishonestyPanel, StudentPanel} from "./panel"
import StudentCommitDiffPanel from './panel/StudentCommitDiffPanel'

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

        this.props.clearCurrentCourse()
        this.props.clearCurrentSemester()

        let course = this.props.match.params.courseID ? this.props.match.params.courseID : defaultCourse
        const page = this.props.path.substring(this.props.path.lastIndexOf('/') + 1)

        if(!/^[0-9]+$/.test(course)) {
            course = defaultCourse
        }

        this.props.setCurrentCourse(course)

        history.push(`/${course}/course`)

        this.props.getAccount(`${url}/api/account`, {
            "Content-Type": "application/json"
        })
    }

    componentDidUpdate(prevProps) {
        if (this.props.location.pathname !== prevProps.location.pathname)
            this.panel.scrollTop = 0;
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
                            <Route path="/:courseID/course" render={(navProps) =>
                                <CoursePanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route exact path="/:courseID/student/:id" render={(navProps) =>
                                <StudentPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path='/:courseID/student/:studentID/commit/:commitID' render={(navProps) =>
                                <StudentCommitDiffPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/admin" render={(navProps) =>
                                <AdminPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/projects" render={(navProps) =>
                                <ProjectPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/manage-tas" render={(navProps) =>
                                <ManageTAPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/course-dishonesty" render={(navProps) =>
                                <CourseDishonestyPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/:courseID/student-dishonesty/:id" render={(navProps) =>
                                <StudentDishonestyPanel ref={ this.setChild } {...navProps}/>
                            }/>
                            <Route path="/:courseID/settings" render={(/* navProps */) =>
                                <PreferencePanel ref={ this.setChild } />
                            }/>
                            <Route path='/' render={(/* navProps */) => <Redirect to={`/${defaultCourse}/course`}/>}/>
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
        clearCurrentCourse: () => dispatch(clearCurrentCourse()),
        clearCurrentSemester: () => dispatch(clearCurrentSemester()),
    }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Main))