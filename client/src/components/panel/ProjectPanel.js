import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav, Summary, Title} from '../Helpers'
import {history} from '../../redux/store'
import {getClassProjects, setCurrentProject} from '../../redux/actions'
import connect from 'react-redux/es/connect/connect'

class ProjectPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            name: '',
            source_name: '',
            created_date: '',
            due_date: ''
        }
    }

    back = () => {
        history.goBack()
    };

    render() {

        const action_names = [
            'Add New Project',
            'Upload Test Zip',
            'Save Changes',
            'Revert Changes',
            'Remove Project'
        ]

        const actions = [
            {},
            {},
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

                <div className='panel-center-content'>
                    <h1 className='header'>CS252 - Projects</h1>
                    <div className='h1 break-line header' />

                    <Summary header={ <h3 className='header'>{this.props.projects && this.props.projects[this.props.current_project_index] ? `${this.props.projects[this.props.current_project_index].project_name} - ` : 'New - '}Properties</h3> }
                        columns={ 2 }
                        data={ [ 1, 2 ] }
                        iterator={ (index) =>
                            index === 1 ?
                                <div>
                                    <h4 className="header">
                                        Name
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="name" autoComplete="off"/>
                                    <h4 className="header">
                                        Source Name
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.source_name} placeholder="Ex. lab1-src, lab2, ..." onChange={this.onChange} name="source_name" ref="source_name" autoComplete="off"/>
                                    <h4 className="header">
                                        Created Date
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.created_date} placeholder="MM/DD/YYYY" onChange={this.onChange} name="created_date" ref="created_date" autoComplete="off"/>
                                    <h4 className="header">
                                        Due Date
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.due_date} placeholder="MM/DD/YYYY" onChange={this.onChange} name="due_date" ref="due_date" autoComplete="off"/>
                                </div> :

                                null
                        } />

                    <div className='h2 break-line' />
                    <h3 className='header'>Test Scripts</h3>
                    <table>
                        <thead>
                            <tr>
                                <td><h4 className='header'>#</h4></td>
                                <td><h4 className='header'>Name</h4></td>
                                <td><h4 className='header'>Point Value</h4></td>
                                <td><h4 className='header'>View Script</h4></td>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>1</td>
                                <td>Test Script 1</td>
                                <td>50</td>
                                <td>10</td>
                            </tr>
                        </tbody>
                    </table>
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
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index))
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(ProjectPanel)