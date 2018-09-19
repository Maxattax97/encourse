import React, { Component } from 'react'

import ProjectNavigation from '../project/ProjectNavigation';
import settingsIcon from '../../img/settings.svg'

class CoursePanel extends Component {

    constructor(props) {
        super(props);

        this.showProjectOptions = this.showProjectOptions.bind(this);
    }

    showProjectOptions() {
        this.props.history.push("/project-settings");
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