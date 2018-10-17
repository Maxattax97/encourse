import React, { Component } from 'react'

import ProjectModal from '../modals/ProjectModal'
import {getClassProjects, setCurrentProject} from '../../redux/actions/index'
import connect from 'react-redux/es/connect/connect'
import {Title, Card} from '../Helpers'
import {back, settings, plus} from '../../helpers/icons'
import {history} from '../../redux/store'

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

    openProjectOptions = () => {
        history.push(`/projects`)
    };

    render() {
        return (
            <div className="projects-nav-container">
                <div className="panel-left-nav">
                    <div className="projects-nav">
                        <div className={ `top-nav svg-icon float-height${ this.props.backClick ? ' action' : '' }` } onClick={ this.props.backClick }>
                            <h3>
                                { this.props.back }
                            </h3>
                            {
                                this.props.backClick ? <img className='svg-icon' src={ back.icon } alt={ back.alt_text } /> : null
                            }
                        </div>
                        <Card component={
                            <div className="projects-container">
                                <Title onClick={ this.openProjectOptions } header={ <h3 className='header'>Projects</h3> } icon={ settings }/>
                                <div className="h3 break-line header"/>
                                <div className='text-list'>
                                    {
                                        this.props.projects &&
                                        this.props.projects.map((project, index) =>
                                            <div key={ project.id }
                                                onClick={ () => this.changeProject(project.id, index) }
                                                className={ `action${this.props.currentProjectIndex === index && !this.state.new_project ? ' projects-highlight' : ''}` }>
                                                <h4>
                                                    { project.project_name }
                                                </h4>
                                            </div>)
                                    }
                                    <div className={ `projects-new action svg-icon${this.state.new_project ? ' projects-highlight' : ''}` } onClick={ this.openProjectOptions }>
                                        <img className='svg-icon' src={ plus.icon } alt={ plus.alt_text } />
                                    </div>
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

                <div className={ `modal-overlay${ this.state.show_project_options ? ' show' : '' }` }
                    style={ this.state.show_project_options ? { } : { 'display': 'none' } }
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