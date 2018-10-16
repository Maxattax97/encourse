import React, { Component } from 'react'
import { connect } from 'react-redux'

import deleteIcon from '../../resources/trash.svg'
import checkmarkIcon from '../../resources/checkmark.svg'
import backIcon from '../../resources/back.svg'
import Modal from './Modal'
import TestScriptList from './util/TestScriptList'
import { addProject, modifyProject, addTest, deleteProject } from '../../redux/actions'
import url from '../../server'


function getEmptyState(props) {
    return {
        show: props.show,
        name: '',
        source_name: '',
        created_date: '',
        due_date: '',
        test_script: [],
        hidden_test_script: [],
        current_project: props.current_project,
        new_project: props.new_project,
        show_test_scripts: false,
        show_hidden_scripts: false
    }
}

function getStateFromProjectsProp(props) {
    if(!props.projects || props.projects.length === 0)
        return getEmptyState(props)

    const project = props.projects[props.current_project]
    return {
        show: props.show,
        name: project.project_name,
        source_name: project.source_name,
        created_date: project.start_date,
        due_date: project.due_date,
        test_script: project.test_script,
        hidden_test_script: project.hidden_test_script,
        current_project: props.current_project,
        new_project: props.new_project,
        show_test_scripts: false,
        show_hidden_scripts: false
    }
}

class ProjectModal extends Component {

    constructor(props) {
        super(props)

        this.state = {
            show: false,
            name : false,
            source_name: '',
            created_date: '',
            due_date: '',
            test_script: [],
            hidden_test_script: [],
            show_test_scripts: false,
            show_hidden_scripts: false
        }

        this.test_script_ref = React.createRef()
        this.hidden_script_ref = React.createRef()
    }

    static getDerivedStateFromProps(props, state) {
        if (props.new_project !== state.new_project) {
            if (state.new_project)
                return getStateFromProjectsProp(props)
            else
                return getEmptyState(props)
        }

        if(state.show !== props.show || props.current_project !== state.current_project)
            return getStateFromProjectsProp(props)
        else
            return state
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
    };

    submitProject = () => {
        if(this.state.new_project) {
            //TODO: remove courseID and semester hardcoding
            this.props.addProject(`${url}/api/add/project`, 
                {'Authorization': `Bearer ${this.props.token}`,
                    'Content-Type': 'application/json'},
                JSON.stringify({
                    courseID: 'cs252',
                    semester: 'Fall2018',
                    projectName: this.state.name,
                    repoName: this.state.source_name,
                    startDate: this.state.created_date,
                    dueDate: this.state.due_date,
                    projectIdentifier: 'cs252 Fall2018:' + this.state.name,
                }))
        }
        else {
            this.props.modifyProject(`${url}/api/modify/project?projectID=${this.props.currentProjectId}`,
                {'Authorization': `Bearer ${this.props.token}`,
                    'Content-Type': 'application/json'},
                JSON.stringify({
                    startDate: this.state.created_date,
                    dueDate: this.state.due_date,
                }))
            for(let script of this.state.test_script) {
                this.postTest(script, false)
            } 
            for(let script of this.state.hidden_test_script) {
                this.postTest(script, true)
            }
        }
        this.props.toggleProjectOptions()
    }

    postTest = (script, isHidden) => {
        if(script.file) {
            if (FileReader) {
                let fr = new FileReader()
                fr.onload = () => {
                    this.props.addTest(`${url}/api/add/test?projectID=${this.props.currentProjectId}&testName=${script.testScriptName}&isHidden=${isHidden}&points=${script.pointValue ? script.pointValue : 5}`,
                        {'Authorization': `Bearer ${this.props.token}`}, btoa(fr.result))
                }
                fr.readAsText(script.file)
            }
            // console.log(script)
        
        }   
        if(script.pointValue && !script.file) {
            //TODO!: Only point value changed, add API call whenever endpoint is changed
        }
       
    }

    deleteProject = () => {
        if(this.state.new_project) {
            this.setState({
                name: '',
                source_name: '',
                created_date: '',
                due_date: '',
                test_script: [],
                hidden_test_script: [],
                show_test_scripts: false,
                show_hidden_scripts: false
            })
        }
        else {
            this.props.deleteProject(`${url}/api/delete/project?projectID=${this.props.currentProjectId}`,
                {'Authorization': `Bearer ${this.props.token}`})
        }
        this.props.toggleProjectOptions()
    };

    render() {

        const modal_buttons =
            <div className="modal-buttons float-height">
                <div onClick={ this.deleteProject }>
                    <img src={deleteIcon} />
                </div>

                <div className="project-options-add" onClick={ this.submitProject }>
                    <img src={ checkmarkIcon } />
                </div>
            </div>

        return (
            <div className="project-options">
                <Modal left
                    show={ this.props.show && !this.state.show_test_scripts && !this.state.show_hidden_scripts }
                    onExit={ this.props.close }
                    component={
                        <div className="panel-project-options">
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
                            <div className="project-options-scripts">
                                <h4 onClick={ () => this.setState({ show_test_scripts: true }) }>
                                       Test Scripts
                                </h4>
                                <h4 onClick={ () => this.setState({ show_hidden_scripts: true }) }>
                                       Hidden Test Scripts
                                </h4>
                            </div>
                            { modal_buttons }
                        </div>
                    } />

                <Modal left
                    show={ this.props.show && this.state.show_test_scripts && !this.state.show_hidden_scripts }
                    onExit={ this.props.close }
                    component={
                        <div className="panel-project-options">
                            <div className="title" onClick={ () => this.setState({ show_test_scripts: false }) }>
                                <img src={ backIcon } />
                                <h4 className="header">
                                       Test Scripts
                                </h4>
                            </div>
                            <h4 className="break-line title" />

                            <TestScriptList script_list={ this.state.test_script }
                                ref={ this.test_script_ref }/>

                            { modal_buttons }
                        </div>
                    } />

                <Modal left
                    show={ this.props.show && !this.state.show_test_scripts && this.state.show_hidden_scripts }
                    onExit={ this.props.close }
                    component={
                        <div className="panel-project-options">
                            <div className="title" onClick={ () => this.setState({ show_hidden_scripts: false }) }>
                                <img src={ backIcon } />
                                <h4 className="header">
                                       Hidden Test Scripts
                                </h4>
                            </div>
                            <h4 className="break-line title" />

                            <TestScriptList script_list={ this.state.hidden_test_script }
                                ref={this.hidden_script_ref}/>

                            { modal_buttons }
                        </div>
                    } />
            </div>
        )
    }

}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        current_project: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        addProject: (url, headers, body) => dispatch(addProject(url, headers, body)),
        deleteProject: (url, headers, body) => dispatch(deleteProject(url, headers, body)),
        modifyProject: (url, headers, body) => dispatch(modifyProject(url, headers, body)),
        addTest: (url, headers, body) => dispatch(addTest(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectModal)