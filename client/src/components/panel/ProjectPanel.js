import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {Summary, Title} from '../Helpers'
import {getClassProjects, setCurrentProject, setModalState} from '../../redux/actions'
import {getCurrentCourseId} from '../../redux/state-peekers/course'
import connect from 'react-redux/es/connect/connect'
import {ProjectInfo, ProjectModal, ProjectTestFilter, ProjectTestModal} from './project'
import {getCurrentProject, getProjects} from '../../redux/state-peekers/projects'
import ProjectSuiteModal from './project/ProjectSuiteModal'
import ProjectSuiteSummary from './project/ProjectSuiteSummary'
import BackNavigation from '../navigation/BackNavigation'

class ProjectPanel extends Component {


    render() {

        const action_names = [
            'Add New Project',
            'Add New Test Suite',
	        'Add New Test Script',
            'Upload Test Zip'
        ]

        const actions = [
	        () => this.props.setModalState(1),
            () => this.props.setModalState(3),
	        () => this.props.setModalState(5),
            {},
        ]

        return (
            <div className='panel-projects'>

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

	            <ProjectModal id={1} newProject />
	            <ProjectModal id={2} />
                <ProjectSuiteModal id={3} newTestSuite/>
                <ProjectSuiteModal id={4}/>
	            <ProjectTestModal id={5} newTestScript />
	            <ProjectTestModal id={6} />

                {
                    this.props.projects.length ?
                        this.props.project ?
                            <div className='panel-center-content'>
                                <Title onClick={ () => this.props.setModalState(2) }>
                                    <h1 className='header'>{ this.props.project.project_name }</h1>
                                </Title>
                                <div className='h1 break-line header' />

                                <h3 className='header'>Project Information</h3>
                                <Summary columns={2}>
                                    <ProjectInfo />
                                </Summary>

                                <div className='h1 break-line' />
                                <ProjectSuiteSummary/>

                                <div className='h1 break-line' />

                                <ProjectTestFilter />
                            </div>
                            :
                            <div className='panel-center-content'>
                                <Title onClick={ () => this.props.setModalState(1) }>
                                    <h1 className='header'>{ 'Select a Project' }</h1>
                                </Title>
                                <div className='h1 break-line header' />
                            </div>
                        :
                        <div className='panel-center-content'>
                            <Title onClick={ () => this.props.setModalState(1) }>
                                <h1 className='header'>{ 'New Project' }</h1>
                            </Title>
                            <div className='h1 break-line header' />
                        </div>
                }
            </div>
        )
    }

}

const mapStateToProps = (state) => {
    return {
        currentCourseId: getCurrentCourseId(state),
        project: getCurrentProject(state),
        projects: getProjects(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
	    setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(ProjectPanel)