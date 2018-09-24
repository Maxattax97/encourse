import React, { Component } from 'react'

import ProjectNavigation from '../project/ProjectNavigation'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'
import settingsIcon from '../../img/settings.svg'
import { history } from '../../redux/store'
import ClassProgressHistogram from '../charts/ClassProgressHistogram'

class CoursePanel extends Component {

    showProjectOptions = () => {
        history.push("/project-settings")
    }

    render() {
        return (
            <div className="panel-course">
                <ProjectNavigation titleClick={this.showProjectOptions} mode={0} />
                <div className="panel-center-content">
                    <h3>Class Statistics</h3>
	                <ClassProgressHistogram/>
                </div>
            </div>
        )
    }
}

export default CoursePanel
