import React, { Component } from 'react'

import settingsIcon from '../../resources/settings.svg'
import backIcon from '../../resources/back.svg'
import plusIcon from '../../resources/plus.svg'
import Card from '../Card'
import ProjectModal from '../modals/ProjectModal'
import {getClassProjects, setCurrentProject} from '../../redux/actions/index'
import connect from 'react-redux/es/connect/connect'
import {Title} from '../Helpers'
import {settings} from '../../helpers/icons'

class ProjectNavigation extends Component {

    constructor(props) {
        super(props)

        this.state = {
            show_project_options : false,
            new_project : false
        }
    }

    toggleProjectOptions = (mode) => {
        this.setState({ show_project_options: mode, new_project: this.props.projects.length === 0 && mode })

        this.props.onModalBlur(mode)
    };

    changeProject = (project_id, project_index) => {
        this.setState({ new_project: false })
        this.props.setCurrentProject(project_id, project_index)
    };

    handleNewProject = () => {
        if(this.state.show_project_options && this.state.new_project) {
            return
        }
        else {
            this.setState({ show_project_options: true, new_project: true })
            this.props.onModalBlur(true)
        }
    };

    render() {
        return (
            <div className="projects-nav-container">
                <div className="panel-left-nav">
                    <div className="projects-nav">
                        <div className={ `back-nav float-height${ this.props.backClick ? ' back-nav-color' : '' }` } onClick={ this.props.backClick }>
                            <h3>
                                { this.props.back }
                            </h3>
                            <img src={ backIcon } alt={'back'} />
                        </div>
                        <Card component={
                            <div className="projects-container">
                                <Title onClick={ () => this.toggleProjectOptions(!this.state.show_project_options) } header={ <h3>Projects</h3> } icon={ settings }/>
                                <h3 className="break-line title"/>
                                {
                                    this.props.projects &&
                                    this.props.projects.map((project, index) =>
                                        <h4 className={ this.props.currentProjectIndex === index && !this.state.new_project ? 'projects-highlight' : '' }
                                            key={ project.id }
                                            onClick={ () => this.changeProject(project.id, index) }>

                                            { project.project_name }
                                        </h4>)
                                }
                                <div className={ `projects-new${this.state.new_project ? ' projects-highlight' : ''}` } onClick={ this.handleNewProject }>
                                    <img src={ plusIcon } />
                                </div>
                            </div>
                        } />
                    </div>
                </div>

                <ProjectModal show={ this.state.show_project_options }
                    close={ () => this.toggleProjectOptions(false) }
                    projects={ this.props.projects }
                    current_project={ this.props.currentProjectIndex }
                    new_project={ this.state.new_project }
                    toggleProjectOptions={ this.toggleProjectOptions }/>

                <div className={`modal-overlay${ this.state.show_project_options ? ' show' : '' }`}
                    onClick={ () => this.toggleProjectOptions(false) } />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        currentProjectIndex: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectNavigation)