import React, { Component } from 'react'

import ProjectNavigation from '../project/ProjectNavigation'
import settingsIcon from '../../img/settings.svg'
import { history } from '../../redux/store'

class CoursePanel extends Component {

    showProjectOptions = () => {
        history.push("/project-settings")
    }

    render() {
        return (
            <div className="panel-course">
                <ProjectNavigation titleClick={this.showProjectOptions} mode={0} />

            </div>
        )
    }
}

export default CoursePanel