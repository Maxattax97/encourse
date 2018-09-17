import React, { Component } from 'react'

import '../css/Navbar.css'
import settingsIcon from '../img/settings.svg'
import logoutIcon from '../img/logout.svg'
import { history } from '../redux/store'

class Navbar extends Component {
    render() {
        return (
            <div className="Navbar">
                <div className="EnCourse-Title">
                    <div>En</div>
                    <div>Course</div>
                </div>
                <div className="Options">
                    <div className="Settings">
                        <img src={settingsIcon} />
                    </div>
                    <div className="Logout">
                        <img src={logoutIcon} />
                    </div>
                </div>
            </div>
        )
    }
}

export default Navbar