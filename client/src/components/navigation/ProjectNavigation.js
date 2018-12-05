import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassProjects, setCurrentProject } from '../../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from "../../redux/state-peekers/course"
import {Title, Card, SettingsIcon, LoadingIcon} from '../Helpers'
import { history } from '../../redux/store'
import {getCurrentProject, getProjects} from '../../redux/state-peekers/projects'
import {retrieveAllProjects} from '../../redux/retrievals/projects'

class ProjectNavigation extends Component {

    componentDidMount = () => {
        if(!this.props.projects.length)
            retrieveAllProjects(this.props.currentCourseId, this.props.currentSemesterId)
    }

    openProjectOptions = () => {
        history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/projects`)
    };

    render() {
        const textListDiv =
            this.props.isLoading && !this.props.projects.length ?
                <div className='loading'>
                    <LoadingIcon/>
                </div>
                :
                this.props.projects.map((project, index) =>
                    <div key={ project.id }
                         onClick={ () => this.props.setCurrentProject(project.id, index) }
                         className={ `action${this.props.project === project ? ' list-highlight' : ''}` }>
                        <h4>
                            { project.project_name }
                        </h4>
                    </div>
                )

        return (
            <div className="list-nav side-nav-left">
                <Card>
                    <div className="list-container">
                        <Title onClick={ this.openProjectOptions }>
                            <h3 className='header'>Projects</h3>
                            <SettingsIcon/>
                        </Title>

                        <div className="h3 break-line header"/>
                        <div className='text-list'>
                            {
                                textListDiv
                            }
                        </div>
                    </div>
                </Card>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        projects: getProjects(state),
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
        project: getCurrentProject(state),
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