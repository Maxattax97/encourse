import React, { Component } from "react"

import plusIcon from "../../img/plus.svg"
import syncIcon from "../../img/sync.svg"
import deleteIcon from "../../img/delete.svg"

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
        new_project: props.new_project
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
            hidden_test_script: false
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
                    new_project: true
                };
        }
        if(state.visible !== props.visible || props.current_project !== state.current_project)
            return getStateFromProjectsProp(props);
        else
            return state;
    }

    onChange = (event) => {
        console.log(event.target.name, event.target.value);
        this.setState({[event.target.name]: event.target.value});
        console.log({[event.target.name]: event.target.value}, this.state);
    };

    render() {
        return (
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
                <h4 className="header">
                    Test Script
                </h4>
                <input type="file" name="test_script" ref="test_script" />
                <h4 className="header">
                    Hidden Test Script
                </h4>
                <input type="file" name="test_script" ref="test_script" />
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
        );
    }

}

export default ProjectOptions;