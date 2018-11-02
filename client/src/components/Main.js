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

    createPanelRef = (node) => {
        if(node) {
            this.panel = node

            this.panel.addEventListener('scroll', this.scrollPanel)
        }
    }

    scrollPanel = (e) => {
        console.log('SCROLL: ', e.currentTarget.scrollHeight, e.currentTarget.scrollTop, e.currentTarget.clientHeight)
        if(e.currentTarget.scrollHeight - e.currentTarget.scrollTop === e.currentTarget.clientHeight && this.currentChild && this.currentChild.scrolledToBottom) {
            this.currentChild.scrolledToBottom()
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
                            <Route path="/panel" render={(/* navProps */) => {
                                //determine logic for course panel, student panel, or admin panel. For now, use course panel
                                return <Redirect to="/course"/>
                            }}/>
                            <Route path="/course" render={(navProps) =>
                                <CoursePanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/student/:id" render={(navProps) =>
                                <StudentPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/admin" render={(navProps) =>
                                <AdminPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/projects" render={(navProps) =>
                                <ProjectPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/manage-tas" render={(navProps) =>
                                <ManageTAPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/course-dishonesty" render={(navProps) =>
                                <CourseDishonestyPanel ref={ this.setChild } {...navProps} />
                            }/>
                            <Route path="/student-dishonesty/:id" render={(navProps) =>
                                <StudentDishonestyPanel ref={ this.setChild } {...navProps}/>
                            }/>
                            <Route path="/settings" render={(/* navProps */) =>
                                <PreferencePanel ref={ this.setChild } />
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