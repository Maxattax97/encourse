import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassProjects, setCurrentProject } from '../../redux/actions'
import { Title, Card, BackIcon, SettingsIcon, PlusIcon } from '../Helpers'
import { history } from '../../redux/store'
import url from '../../server'

class ProjectNavigation extends Component {

    constructor(props) {
        super(props)

        this.state = {
            show_project_options : false,
            new_project : false
        }
    }

    componentDidMount = () => {
        if(this.props.projects.length === 0) {
            //TODO: remove classid and semester hardcoding
            this.props.getClassProjects(`${url}/api/projectsData?courseID=cs252&semester=Fall2018`)
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
        history.push('/projects')
    };

    render() {
        return (
            <div className="project-nav-container">
                <div className="panel-left-nav">
                    <div className="list-nav side-nav-left">
                        <div className={ `top-nav back-nav svg-icon float-height${ this.props.backClick ? ' action' : '' }` } onClick={ this.props.backClick }>
                            <h3>
                                { this.props.back }
                            </h3>
                            {
                                this.props.backClick ? <BackIcon/> : null
                            }
                        </div>
                        <Card>
                            <div className="list-container">
                                <Title onClick={ this.openProjectOptions } header={ <h3 className='header'>Projects</h3> } icon={ <SettingsIcon/> }/>
                                <div className="h3 break-line header"/>
                                <div className='text-list'>
                                    {
                                        this.props.projects &&
                                        this.props.projects.map((project, index) =>
                                            <div key={ project.id }
                                                onClick={ () => this.changeProject(project.id, index) }
                                                className={ `action${this.props.currentProjectIndex === index && !this.state.new_project ? ' list-highlight' : ''}` }>
                                                <h4>
                                                    { project.project_name }
                                                </h4>
                                            </div>)
                                    }
                                    <div className={ `list-new action svg-icon${this.state.new_project ? ' list-highlight' : ''}` } onClick={ this.openProjectOptions }>
                                        <PlusIcon/>
                                    </div>
                                </div>
                            </div>
                        </Card>
                    </div>
                </div>
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