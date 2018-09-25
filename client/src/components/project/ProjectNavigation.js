import React, { Component } from 'react'

import settingsIcon from '../../img/settings.svg'
import exitIcon from '../../img/x.svg'

class ProjectNavigation extends Component {

    render() {
        return (
            <div className="panel-left-nav">
                <div className="projects-nav">
                    <div className="title" onClick={this.props["titleClick"]}>
                        <h3>
                            Projects
                        </h3>
                        {this.props.mode === 0 ? <img src={settingsIcon} /> : <img src={exitIcon} />}
                    </div>
                    <h3 className="break-line title" />
                    <h4>
                        MyMalloc1
                    </h4>
                    <h4>
                        Shell Project - Part 1
                    </h4>
                    <h4>
                        Shell Project - Part 2
                    </h4>
                </div>
            </div>
        )
    }
}

export default ProjectNavigation