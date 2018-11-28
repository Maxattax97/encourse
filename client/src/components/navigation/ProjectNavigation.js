import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassProjects, setCurrentProject } from '../../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from "../../redux/state-peekers/course"
import {Title, Card, SettingsIcon, LoadingIcon} from '../Helpers'
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
            this.props.getClassProjects(`${url}/api/projectsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}`)
        }
    }

    changeProject = (project_id, project_index) => {
        this.setState({ new_project: false })
        this.props.setCurrentProject(project_id, project_index)
    };

    openProjectOptions = () => {
        history.push('/projects')
    };

    render() {
        return (
            <div className="list-nav side-nav-left">
                <Card>
                    <div className="list-container">
                        <Title onClick={ this.openProjectOptions }>
                            <h3 className='header'>Projects</h3>
                            <SettingsIcon/>
                        </Title>
                        <div className="h3 break-line header"/>
                        {
                            !this.props.isLoading ?
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
                                </div>
                                :
                                <div className='loading'>
                                    <LoadingIcon/>
                                </div>
                        }
                    </div>
                </Card>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
        currentProjectIndex: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0,
        isLoading: state.projects ? state.projects.getClassProjectsIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectNavigation)