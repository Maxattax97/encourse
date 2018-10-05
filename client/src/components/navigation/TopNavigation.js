import React, { Component } from 'react'
import { connect } from 'react-redux'

import settingsIcon from '../../img/settings.svg'
import logoutIcon from '../../img/logout.svg'
import { history } from '../../redux/store'
import { logOut } from '../../redux/actions/index'
import url from '../../server'

class TopNavigation extends Component {

    logOut = () => {
        this.props.logOut(`${url}/api/logout`, {
            'Authorization': `Bearer ${this.props.token}`
        })
    }

    render() {
        return (
            <div className="nav">
                <div className="nav-list">
                    <div className="nav-options">
                        <div className="nav-settings" onClick={() => {
                            if(this.props.path === '/settings') history.push('/course')
                            else history.push('/settings')
                        }}>
                            <img src={settingsIcon} alt="options" />
                        </div>
                        <div className="nav-logout" onClick={() => this.logOut()}>
                            <img src={logoutIcon} alt="logout" />
                        </div>
                    </div>
                </div>
                <div className="nav-title">
                    <span className="nav-en">En</span>
                    <span className="nav-course">Course</span>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return { 
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        path: state.router && state.router.location ? state.router.location.pathname : null,
    }
}
  
const mapDispatchToProps = (dispatch) => {
    return {
        logOut: (url, headers) => dispatch(logOut(url, headers))
    }
}
  
export default connect(mapStateToProps, mapDispatchToProps)(TopNavigation)