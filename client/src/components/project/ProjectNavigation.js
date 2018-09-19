import React, { Component } from 'react'

import settingsIcon from '../../img/settings.svg'

class ProjectNavigation extends Component {

    render() {
        return (
            <div className="panel-left-nav">
                <div className="projects-nav">
                    <div className="projects-title">
                        <h3>
                            Projects
                        </h3>
                        <img src={settingsIcon} />
                    </div>
                    <h3 className="break-line title" />
                    <h4>
                        MyMalloc1
                    </h4>
                    <h4>
                        MyMalloc2
                    </h4>
                    <h4>
                        MyMalloc3
                    </h4>
                </div>
            </div>
        )
    }
}

export default ProjectNavigation