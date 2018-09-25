import React, { Component } from "react"

class ProjectOptions extends Component {

    render() {
        return (
            <div className="panel-project-options">
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
        );
    }

}

export default ProjectOptions;