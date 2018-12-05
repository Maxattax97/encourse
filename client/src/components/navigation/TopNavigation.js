import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import { logOut } from '../../redux/actions/index'
import { getCurrentCourseId, getCurrentSemesterId } from '../../redux/state-peekers/course'
import url from '../../server'
import {LogoutIcon, SettingsIcon} from '../Helpers'

class TopNavigation extends Component {

    logOut = () => {
        this.props.logOut(`${url}/api/logout`)
    }

    render() {
        return (
            <div className="nav">
                <div className="nav-options">
                    <div className="action" onClick={() => {
                        if(this.props.path.includes('/settings')) history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`)
                        else history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/settings`)
                    }}>
                        <SettingsIcon/>
                    </div>
                    <div className="action" onClick={() => this.logOut()}>
                        <LogoutIcon/>
                    </div>
                </div>
                <div className="nav-title" onClick={ () => history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`)}>
                    <span className="nav-en">En</span>
                    <span className="nav-course">Course</span>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        path: state.router && state.router.location ? state.router.location.pathname : null,    
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        logOut: (url, headers) => dispatch(logOut(url, headers))
    }
}
  
export default connect(mapStateToProps, mapDispatchToProps)(TopNavigation)