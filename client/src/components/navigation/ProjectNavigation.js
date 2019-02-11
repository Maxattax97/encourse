import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassProjects, setCurrentProject } from '../../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from "../../redux/state-peekers/course"
import {Title, Card, SettingsIcon, LoadingIcon} from '../Helpers'
import { history } from '../../redux/store'
import {getCurrentProject, getProjects} from '../../redux/state-peekers/projects'
import {retrieveAllProjects} from '../../redux/retrievals/projects'
import {getAccount} from '../../redux/state-peekers/auth'
import {isAccountNotTA} from '../../common/state-helpers'

class ProjectNavigation extends Component {

    componentDidMount = () => {
        if(!this.props.projects.length && this.props.currentCourseId)
            retrieveAllProjects(this.props.currentCourseId)
    }

    componentDidUpdate = (prevProps) => {
        if(this.props.currentCourseId !== prevProps.currentCourseId)
            retrieveAllProjects(this.props.currentCourseId)
    }

    openProjectOptions = () => {
        history.push(`/${this.props.currentCourseId}/projects`)
    };

    render() {
        const textListDiv =
            this.props.isLoading && !this.props.projects.length ?
                <div className='loading'>
                    <LoadingIcon/>
                </div>
                :
                this.props.projects.map((project, index) =>
                    <div key={ project.projectID }
                         onClick={ () => this.props.setCurrentProject(project.projectID, index) }
                         className={ `action${this.props.project === project ? ' list-highlight' : ''}` }>
                        <h4>
                            { project.name }
                        </h4>
                    </div>
                )

        return (
            <div className="list-nav side-nav-left">
                <Card>
                    <div className="list-container">
                        {
                            false ?
                                <Title onClick={ this.openProjectOptions }>
                                    <h3 className='header'>Projects</h3>
                                    <SettingsIcon/>
                                </Title>
                                :
                                <Title>
                                    <h3 className='header'>Projects</h3>
                                </Title>
                        }

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
        project: getCurrentProject(state),
        isLoading: state.projects ? state.projects.getClassProjectsIsLoading : false,
        account: getAccount(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectNavigation)