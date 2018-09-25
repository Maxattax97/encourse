import React, { Component } from 'react'

import settingsIcon from '../../img/settings.svg'

class ProjectNavigation extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="panel-left-nav">
                <div className="projects-nav">
                    <div className="title" onClick={this.props["titleClick"]}>
                        <h3>
                            Projects
                        </h3>
                        <img src={settingsIcon} />
                    </div>
                    <h3 className="break-line title" />
                    {
                        this.props.info.map((project, index) => <h4 className={this.props.currentProject === index ? "projects-highlight" : ""} key={project.id} onClick={() => this.props.projectClick(index)}>{project.name}</h4>)
                    }
                </div>
            </div>
        )
    }
}

export default ProjectNavigation