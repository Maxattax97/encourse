import React, { Component } from "react"

import deleteIcon from "../../img/delete.svg"
import checkmarkIcon from "../../img/checkmark.svg"
import backIcon from "../../img/back.svg"
import Modal from "./Modal";
import TestScriptList from "./util/TestScriptList";
import { addProject } from "../../redux/actions";
import url from '../../server'
import { connect } from "react-redux"

function getEmptyState(props) {
    return {
        show: props.show,
        name: "",
        source_name: "",
        created_date: "",
        due_date: "",
        test_script: [],
        hidden_test_script: [],
        current_project: props.current_project,
        new_project: props.new_project,
        show_test_scripts: false,
        show_hidden_scripts: false
    };
}

function getStateFromProjectsProp(props) {
    if(!props.projects || props.projects.length === 0)
        return getEmptyState(props);

    const project = props.projects[props.current_project];
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
    };
}

class ProjectModal extends Component {

    constructor(props) {
        super(props);

        this.state = {
            show: false,
            name : false,
            source_name: "",
            created_date: "",
            due_date: "",
            test_script: [],
            hidden_test_script: [],
            show_test_scripts: false,
            show_hidden_scripts: false
        };

        this.test_script_ref = React.createRef();
        this.hidden_script_ref = React.createRef();
    }

    static getDerivedStateFromProps(props, state) {
        //console.log(props, state);
        if (props.new_project !== state.new_project) {
            if (state.new_project)
                return getStateFromProjectsProp(props);
            else
                return getEmptyState(props);
        }

        if(state.show !== props.show || props.current_project !== state.current_project)
            return getStateFromProjectsProp(props);
        else
            return state;
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value });
    };

    submitProject = () => {
        if(this.state.new_project) {
            //TODO Bucky add new project
            this.props.addProject(`${url}/secured/add/project`, 
            {'Authorization': `Bearer ${this.props.token}`},
            {
                courseID: 'cs252',
                semester: 'Fall2018',
                projectName: this.state.name,
                repoName: this.source_name,
                startDate: this.created_date,
                endDate: this.due_date,
            })
        }
        else {
            //TODO Bucky update new project
        }
    };

    deleteProject = () => {
        if(this.state.new_project) {
            this.setState({
                name: "",
                source_name: "",
                created_date: "",
                due_date: "",
                test_script: [],
                hidden_test_script: [],
                show_test_scripts: false,
                show_hidden_scripts: false
            })
        }
        else {
            //TODO Bucky delete project at given project index
        }
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
            </div>;

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
        );
    }

}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        current_project: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        addProject: (url, headers, body) => dispatch(addProject(url, headers, body))
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(ProjectModal);