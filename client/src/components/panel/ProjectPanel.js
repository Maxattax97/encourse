import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav, SettingsIcon, Title} from '../Helpers'
import {history} from '../../redux/store'
import {getClassProjects, setCurrentProject, setModalState} from '../../redux/actions'
import connect from 'react-redux/es/connect/connect'
import ProjectTestFilter from "./project/ProjectTestFilter"
import url from "../../server"
import ProjectModal from "./project/ProjectModal"
import ProjectTestModal from "./project/ProjectTestModal"
import HistoryText from "./common/HistoryText"

class ProjectPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            name: '',
            source_name: '',
            created_date: '',
            due_date: '',
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
		this.state.filters[key] = value
		this.setState({ filters: Object.assign({}, this.state.filters) }, () => {
			this.props.resetStudentsPage()
			this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&size=10&page=1&projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy()}&order=${this.state.filters.order_by}&commit=${this.state.filters.commit_filter}&progress=${this.state.filters.progress_filter}&hour=${this.state.filters.hour_filter}`)
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
                    <div className='h6 break-line header' />

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