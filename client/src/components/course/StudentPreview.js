import React, { Component } from 'react';

class StudentPreview extends Component {
    render() {
        return (
            <div className="student-preview">
                <div className="title">
                    <h4>{this.props.info.first_name}</h4>
                    <h4>{this.props.info.last_name}</h4>
                </div>
                <h4 className="break-line title" />
                <div className="student-preview-commits">
                    <h5>Time: {this.props.info.timeSpent[this.props.project]}</h5>
                    <h5>Commits: {this.props.info.commitCounts[this.props.project]}</h5>
                </div>
                <div className="student-preview-progress">
                    <div className="progress-bar">
                        {/*TODO: <div style={{width: this.props.info.progress[this.props.project] + "%"}} /> */}
                        <div style={{width: 30 + "%"}} />
                    </div>
                    <div className="progress-text">
                    {/*TODO: {this.props.info.progress[this.props.project]}%*/}
                        30%
                    </div>
                </div>
            </div>
        );
    }
}

export default StudentPreview;
