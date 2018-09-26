import React, { Component } from "react"

class ProjectOptions extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name : "",
            source_name: "",
            created_date: "",
            due_date: "",
            test_script: false,
            hidden_test_script: false
        }
    }

    static getDerivedStateFromProps(props, state) {
        return {
            name: props.project.name,
            source_name: props.project.source_name,
            created_date: props.project.created_date,
            due_date: props.project.due_date,
            test_script: props.project.test_script,
            hidden_test_script: props.project.hidden_test_script
        };
    }

    onChange = (event) => {
        this.setState({[event.target.name]: event.target.value});
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
                <input type="file" />
                <h4 className="header">
                    Hidden Test Script
                </h4>
                <input type="file" />
            </div>
        );
    }

}

export default ProjectOptions;