import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import { logOut } from '../../redux/actions/index'
import url from '../../server'
import { defaultCourse } from '../../defaults'
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
                        if(this.props.path === '/settings') history.push(`/course/${defaultCourse}`)
                        else history.push('/settings')
                    }}>
                        <SettingsIcon/>
                    </div>
                    <div className="action" onClick={() => this.logOut()}>
                        <LogoutIcon/>
                    </div>
                </div>
                <div className="nav-title" onClick={ () => history.push(`/course/${defaultCourse}`)}>
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
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        logOut: (url, headers) => dispatch(logOut(url, headers))
    }
}
  
export default connect(mapStateToProps, mapDispatchToProps)(TopNavigation)