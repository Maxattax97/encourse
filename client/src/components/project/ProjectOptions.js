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
            <div className="panel-project-options">
                <div className="panel-center-content">
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
                <Exit onClick={this.props.onExit}/>
            </div>
        );
    }

}

export default ProjectOptions;