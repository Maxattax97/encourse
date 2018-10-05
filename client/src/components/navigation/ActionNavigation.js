import React, { Component } from 'react'
import Card from "../Card";

class ActionNavigation extends Component {

    constructor(props) {
        super(props);

        this.state = {
            view_hidden_tests: false
        }
    }

    swapTestView = () => {
        this.setState({ view_hidden_tests: !this.state.view_hidden_tests });

        //TODO Bucky swap the views
    };

    render() {
        return (
            <Card component={
                <div className="student-actions-container">
                    <div className="title">
                        <h3>Actions</h3>
                    </div>
                    <h3 className="break-line title" />
                    <h4 className="student-action-view" onClick={ this.swapTestView }>
                        { this.state.view_hidden_tests ? "View Visible Tests" : "View Hidden Tests" }
                    </h4>
                    <h4 className="student-action-test">
                        Run Tests
                    </h4>
                    <h4 className="student-action-hidden-test">
                        Run Hidden Tests
                    </h4>
                    <h4 className="student-action-pull-repo">
                        Pull Repository
                    </h4>
                    <h4 className="student-action-view-report">
                        View Plagiarism Report
                    </h4>
                </div>
            } />
        )
    }
}

export default ActionNavigation;