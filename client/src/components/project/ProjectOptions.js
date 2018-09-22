import React, { Component } from "react"

import ProjectNavigation from "./ProjectNavigation"
import Exit from "../Exit";
import { history } from '../../redux/store'

class ProjectOptions extends Component {

    exitProjectOptions = () => {
        history.push("/course")
    }

    render() {
        return (
            <div className="panel-project-settings">
                <ProjectNavigation titleClick={this.exitProjectOptions} mode={1}/>
                <div className="panel-center-content">
                    <h3 className="header">
                        Course Projects Directory
                    </h3>
                    <input type="text" className="h2-size" />
                    <h2 className="break-line" />
                    <h4 className="header">
                        Name
                    </h4>
                    <input type="text" className="h3-size"/>
                    <h4 className="header">
                        Source Name
                    </h4>
                    <input type="text" className="h3-size"/>
                    <h4 className="header">
                        Created Date
                    </h4>
                    <input type="text" className="h3-size"/>
                    <h4 className="header">
                        Due Date
                    </h4>
                    <input type="text" className="h3-size"/>
                    <h4 className="header">
                        Test Script
                    </h4>
                    <input type="file" />
                    <h4 className="header">
                        Hidden Test Script
                    </h4>
                    <input type="file" />
                </div>
                <Exit onClick={this.exitProjectOptions}/>
            </div>
        );
    }

}

export default ProjectOptions;