import React, { Component } from 'react'

import settingsIcon from '../../img/settings.svg'
import backIcon from "../../img/back.svg"
import plusIcon from "../../img/plus.svg"
import Card from "../Card";
import ProjectOptions from "./ProjectOptions";
import {getClassProjects, setCurrentProject} from "../../redux/actions";
import connect from "react-redux/es/connect/connect";

class ProjectNavigation extends Component {

    constructor(props) {
        super(props);

        this.state = {
            show_project_options : false,
            new_project : false
        }
    }

    toggleProjectOptions = (mode) => {
        this.setState({ show_project_options: mode, new_project: false });

        this.props.onModalBlur(mode);
    };

    changeProject = (project_index) => {
        this.props.setCurrentProject(project_index);
    };

    handleNewProject = () => {
        if(this.state.show_project_options && this.state.new_project) {

        }
        else
            this.setState({ show_project_options: true, new_project: true });
    };

    render() {
        return (
            <div className="projects-nav-container">
                <div className="panel-left-nav">
                    <div className="projects-nav">
                        <div className={ `back-nav float-height${ this.props.backClick ? " back-nav-color" : "" }` } onClick={ this.props.backClick }>
                            <h3>
                                { this.props.back }
                            </h3>
                            <img src={ backIcon } alt={"back"} />
                        </div>
                        <Card component={
                            <div className="projects-container">
                                <div className="title" onClick={ () => this.toggleProjectOptions(!this.state.show_project_options) }>
                                    <h3>
                                        Projects
                                    </h3>
                                    <img src={ settingsIcon }/>
                                </div>
                                <h3 className="break-line title"/>
                                {
                                    this.props.info.map((project, index) =>
                                        <h4 className={ this.props.currentProject === index ? "projects-highlight" : "" }
                                            key={ project.id }
                                            onClick={ () => this.changeProject(index) }>

                                            { project.name }
                                        </h4>)
                                }
                                <div className="projects-new" onClick={ this.handleNewProject }>
                                    <img src={ plusIcon } />
                                </div>
                            </div>
                        } />
                    </div>
                </div>

                <ProjectOptions show={ this.state.show_project_options }
                                close={ () => this.toggleProjectOptions(false) }
                                projects={ this.props.info }
                                current_project={ this.props.currentProject }
                                new_project={ this.state.new_project }/>

                <div className={`modal-overlay${ this.state.show_project_options ? " show" : "" }`}
                     onClick={ () => this.toggleProjectOptions(false) } />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        projects: state.course && state.course.getClassProjectsData ? state.course.getClassProjectsData : null,
        currentProject: state.projects && state.projects.currentProject ? state.projects.currentProject : 0
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
        setCurrentProject: (project) => dispatch(setCurrentProject(project))
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(ProjectNavigation);