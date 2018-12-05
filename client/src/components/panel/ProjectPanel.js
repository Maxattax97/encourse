import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav, SettingsIcon, Title} from '../Helpers'
import {history} from '../../redux/store'
import {getClassProjects, setCurrentProject, setModalState} from '../../redux/actions'
import {getCurrentCourseId} from '../../redux/state-peekers/course'
import connect from 'react-redux/es/connect/connect'
import {ProjectInfo, ProjectModal, ProjectTestFilter, ProjectTestModal} from './project'
import HistoryText from "./common/HistoryText"
import {getCurrentProject} from '../../redux/state-peekers/projects'

class ProjectPanel extends Component {

    back = () => {
        history.goBack()
    };

    render() {

        const action_names = [
            'Add New Project',
	        'Add New Test Script',
            'Upload Test Zip'
        ]

        const actions = [
	        () => this.props.setModalState(1),
	        () => this.props.setModalState(3),
            {},
        ]

        return (
            <div className='panel-projects'>

                <div className='panel-left-nav'>
                    <BackNav back='Course' backClick={ this.back }/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

	            <div className='panel-right-nav'>
		            <HistoryText />
	            </div>

	            <ProjectModal id={1} newProject />
	            <ProjectModal id={2} />
	            <ProjectTestModal id={3} newTestScript />
	            <ProjectTestModal id={4} />

                <div className='panel-center-content'>
	                <Title onClick={ () => this.props.setModalState(2) }>
		                <h1 className='header'>{ this.props.project ? this.props.project.project_name : '' }</h1>
		                <SettingsIcon/>
	                </Title>
                    <div className='h1 break-line header' />

	                <h3 className='header'>Project Information</h3>
	                <ProjectInfo />

	                <div className='h1 break-line' />

	                <h3 className='header'>Test Scripts</h3>
                    <ProjectTestFilter />
                </div>
            </div>
        )
    }

}

const mapStateToProps = (state) => {
    return {
        currentCourseId: getCurrentCourseId(state),
        project: getCurrentProject(state),
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