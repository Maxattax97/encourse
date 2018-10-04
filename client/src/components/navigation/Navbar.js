import React, { Component } from 'react'
import { connect } from 'react-redux'

import settingsIcon from '../../img/settings.svg'
import logoutIcon from '../../img/logout.svg'
import { history } from '../../redux/store'
import { logOut } from '../../redux/actions/index'
import url from '../../server'

class Navbar extends Component {

    logOut = () => {
        this.props.logOut(`${url}/secured/logout`, {
            'Authorization': `Bearer ${this.props.token}`
        })
    }

    render() {
        return (
            <div className="nav">
                <div className="nav-list">
                    <div className="nav-options">
                        <div className="nav-settings" onClick={() => history.push('/settings')}>
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
    }
}
  
const mapDispatchToProps = (dispatch) => {
    return {
        logOut: (url, headers) => dispatch(logOut(url, headers))
    }
}
  
export default connect(mapStateToProps, mapDispatchToProps)(Navbar)