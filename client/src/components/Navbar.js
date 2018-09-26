import React, { Component } from 'react'
import { connect } from 'react-redux'

import settingsIcon from '../img/settings.svg'
import logoutIcon from '../img/logout.svg'
import { history } from '../redux/store'
import { logOut } from '../redux/actions'

class Navbar extends Component {
    render() {
        return (
            <div className="nav">
                <div className="nav-list">
                    <div className="nav-options">
                        <div className="nav-settings" onClick={() => history.push('/settings')}>
                            <img src={settingsIcon} alt="options" />
                        </div>
                        <div className="nav-logout" onClick={() => this.props.logOut()}>
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
  
const mapDispatchToProps = (dispatch) => {
    return {
        logOut: () => dispatch(logOut())
    }
}
  
export default connect(null, mapDispatchToProps)(Navbar)