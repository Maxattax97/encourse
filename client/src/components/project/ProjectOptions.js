import React, { Component } from "react"

function getStateFromProjectsProp(props) {
    const project = props.projects[props.current_project];
    return {
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
            name : undefined,
            source_name: "",
            created_date: "",
            due_date: "",
            test_script: false,
            hidden_test_script: false
        }
    }

    static getDerivedStateFromProps(props, state) {
        if(state.name) {
            if (props.new_project !== state.new_project)
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
            else if (props.current_project !== state.current_project)
                return getStateFromProjectsProp(props);
            else
                return state;
        }
        else
            return getStateFromProjectsProp(props);
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
                <input type="text" className="h3-size" value={this.state.source_name} onChange={this.onChange} name="source_name" ref="source_name"/>
                <h4 className="header">
                    Created Date
                </h4>
                <input type="text" className="h3-size" value={this.state.created_date} onChange={this.onChange} name="created_date" ref="created_date"/>
                <h4 className="header">
                    Due Date
                </h4>
                <input type="text" className="h3-size" value={this.state.due_date} onChange={this.onChange} name="due_date" ref="due_date"/>
                <h4 className="header">
                    Test Script
                </h4>
                <input type="file" name="test_script" ref="test_script" />
                <h4 className="header">
                    Hidden Test Script
                </h4>
                <input type="file" name="test_script" ref="test_script" />
            </div>
        );
    }

}

export default ProjectOptions;