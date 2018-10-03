import React, { Component } from "react"

import plusIcon from "../../img/plus.svg"
import syncIcon from "../../img/sync.svg"
import deleteIcon from "../../img/delete.svg"
import Modal from "../Modal";

function getStateFromProjectsProp(props) {
    const project = props.projects[props.current_project];
    return {
        visible: props.visible,
        name: project.name,
        source_name: project.source_name,
        created_date: project.created_date,
        due_date: project.due_date,
        test_script: project.test_script,
        hidden_test_script: project.hidden_test_script,
        current_project: props.current_project,
        new_project: props.new_project,
        show_test_scripts: false,
        show_hidden_scripts: false
    };
}

class ProjectOptions extends Component {

    constructor(props) {
        super(props);

        this.state = {
            visible: false,
            name : false,
            source_name: "",
            created_date: "",
            due_date: "",
            test_script: false,
            hidden_test_script: false,
            show_test_scripts: false,
            show_hidden_scripts: false
        }
    }

    static getDerivedStateFromProps(props, state) {
        if (props.new_project !== state.new_project) {
            if (state.new_project)
                return getStateFromProjectsProp(props);
            else
                return {
                    name: "",
                    source_name: "",
                    created_date: "",
                    due_date: "",
                    test_script: [],
                    hidden_test_script: [],
                    current_project: props.current_project,
                    new_project: true,
                    show_test_scripts: false,
                    show_hidden_scripts: false
                };
        }

        if(state.visible !== props.visible || props.current_project !== state.current_project)
            return getStateFromProjectsProp(props);
        else
            return state;
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value });
    };

    render() {
        return (
            <div className="project-options">
                <Modal center
                       show={ this.props.show && !this.state.show_test_scripts && !this.state.show_hidden_scripts }
                       onExit={ this.props.close }
                       component={
                           <div className="panel-project-options">
                               <h4 className="header">
                                   Name
                               </h4>
                               <input type="text" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="name"/>
                               <h4 className="header">
                                   Source Name
                               </h4>
                               <input type="text" className="h3-size" value={this.state.source_name} placeholder="Ex. lab1-src, lab2, ..." onChange={this.onChange} name="source_name" ref="source_name"/>
                               <h4 className="header">
                                   Created Date
                               </h4>
                               <input type="text" className="h3-size" value={this.state.created_date} placeholder="MM-DD-YYYY" onChange={this.onChange} name="created_date" ref="created_date"/>
                               <h4 className="header">
                                   Due Date
                               </h4>
                               <input type="text" className="h3-size" value={this.state.due_date} placeholder="MM-DD-YYYY" onChange={this.onChange} name="due_date" ref="due_date"/>
                               <div className="project-options-scripts">
                                   <h4 onClick={ () => this.setState({ show_test_scripts: true }) }>
                                       Test Scripts
                                   </h4>
                                   <h4 onClick={ () => this.setState({ show_hidden_scripts: true }) }>
                                       Hidden Test Scripts
                                   </h4>
                               </div>
                               <div className="modal-buttons float-height">
                                   <div>
                                       <img src={deleteIcon} />
                                   </div>
                                   {
                                       this.state.new_project ?
                                           <div className="project-options-add">
                                               <img src={plusIcon} />
                                           </div>
                                           :
                                           <div className="project-options-sync">
                                               <img src={syncIcon} />
                                           </div>
                                   }
                               </div>
                           </div>
                       } />

                <Modal center
                       show={ this.props.show && this.state.show_test_scripts && !this.state.show_hidden_scripts }
                       onExit={ this.props.close }
                       onClose={ () => this.setState({ show_test_scripts: false }) }
                       component={
                           <div className="panel-project-options">
                               <h4 className="header">
                                   Test Scripts
                               </h4>
                               <div className="project-options-script-list">

                               </div>
                           </div>
                       } />

                <Modal center
                       show={ this.props.show && !this.state.show_test_scripts && this.state.show_hidden_scripts }
                       onExit={ this.props.close }
                       onClose={ () => this.setState({ show_hidden_scripts: false }) }
                       component={
                           <div className="panel-project-options">

                           </div>
                       } />
            </div>
        );
    }

}

export default ProjectOptions;