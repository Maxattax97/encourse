import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav, SettingsIcon, Title} from '../Helpers'
import {history} from '../../redux/store'
import {getClassProjects, setCurrentProject, setModalState} from '../../redux/actions'
import connect from 'react-redux/es/connect/connect'
import {ProjectInfo, ProjectModal, ProjectTestFilter, ProjectTestModal} from './project'
import HistoryText from "./common/HistoryText"

class ProjectPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            filters: {
	            sort_by: 0,
	            bundle_by: 0,
	            order_by: 0,
                view_filter: 0
            }
        }
    }

    back = () => {
        history.goBack()
    };

	changeFilter = (key, value) => {
        let filters = [...this.state.filters]
        filters[key] = value
		this.setState({ filters }, () => {
		})
	}

    render() {

        const action_names = [
            'Add New Project',
	        'Add New Test Script',
            'Upload Test Zip',
            //'Save Changes',
            //'Revert Changes',
            //'Remove Project'
        ]

        const actions = [
	        () => this.props.setModalState(1),
	        () => this.props.setModalState(3),
            {},
            {},
            {}
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
		                <h1 className='header'>CS252 - Projects - { /*this.props.projects[this.props.current_project_index].project_name*/ }</h1>
		                <SettingsIcon/>
	                </Title>
                    <div className='h1 break-line header' />

	                <h3 className='header'>Project Information</h3>
	                <ProjectInfo />

	                <div className='h1 break-line' />

	                <h3 className='header'>Test Scripts</h3>
                    <ProjectTestFilter onChange={ this.changeFilter } filters={ this.state.filters }/>
                </div>
            </div>
        )
    }

}

const mapStateToProps = (state) => {
    return {
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        current_project_index: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0
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