import React, { Component } from 'react'

import settingsIcon from '../../img/settings.svg'
import backIcon from "../../img/back.svg"
import plusIcon from "../../img/plus.svg"
import Card from "../Card";

class ProjectNavigation extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="panel-left-nav">
                <div className="projects-nav">
                    <div className={"back-nav float-height " + (this.props.backClick ? "back-nav-color" : "")} onClick={this.props.backClick}>
                        <h3>
                            {this.props.back}
                        </h3>
                        <img src={backIcon} alt={"back"} />
                    </div>
                    <Card component={
                        <div className="projects-container">
                            <div className="title" onClick={this.props["titleClick"]}>
                                <h3>
                                    Projects
                                </h3>
                                <img src={settingsIcon}/>
                            </div>
                            <h3 className="break-line title"/>
                            {
                                this.props.info.map((project, index) => <h4
                                    className={this.props.currentProject === index ? "projects-highlight" : ""}
                                    key={project.id} onClick={() => this.props.projectClick(index)}>{project.name}</h4>)
                            }
                            <div className="projects-new">
                                <img src={plusIcon} />
                            </div>
                        </div>
                    } />
                </div>
            </div>
        )
    }
}

export default ProjectNavigation