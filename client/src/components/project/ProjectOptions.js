import React, { Component } from "react"
import settingsIcon from "../../img/settings.svg";
import ProjectNavigation from "./ProjectNavigation";

class ProjectOptions extends Component {

    render() {
        return (
            <div className="panel-project-settings">
                <ProjectNavigation />
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
            </div>
        );
    }

}

export default ProjectOptions;