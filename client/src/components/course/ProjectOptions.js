import React, { Component } from "react"
import settingsIcon from "../../img/settings.svg";

class ProjectOptions extends Component {

    render() {
        return (
            <div className="Project-Settings-Panel">
                <div className="Content">
                    <div className="Projects-Directory">
                        <input type="text" />
                    </div>
                    <div className="Project-Settings">
                        <div className="Project-Name">
                            <input type="text" />
                        </div>
                        <div className="Project-Source-Name">
                            <input type="text" />
                        </div>
                        <div className="Project-Created-Date">
                            <input type="text" />
                        </div>
                        <div className="Project-Due-Date">
                            <input type="text" />
                        </div>
                        <div className="Project-Test-Script">
                            <input type="file" />
                        </div>
                        <div className="Project-Hidden-Test-Script">
                            <input type="file" />
                        </div>
                        <div className="Project-Submit">
                            <input type="submit" />
                        </div>
                    </div>
                </div>
            </div>
        );
    }

}

export default ProjectOptions;