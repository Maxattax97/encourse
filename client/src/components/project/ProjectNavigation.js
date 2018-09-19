import React, { Component } from 'react'

import settingsIcon from '../../img/settings.svg'

class ProjectNavigation extends Component {

    render() {
        return (<div className="panel-left-nav">
                <div className="projects-nav">
                    <div className="projects-title" onClick={ this.props["titleClick"]() }>
                        <h3>
                            Projects
                        </h3>
                        <img src={settingsIcon} />
                    </div>
                    <h3 className="break-line title" />
                    <div className="Project">
                        MyMalloc1
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                    <div className="Project">
                        MyMalloc2
                    </div>
                </div>
                <div className="Course-Content">
                </div>
            </div>
        )
    }
}

export default ProjectNavigation