import React, { Component } from 'react';

class StudentPreview extends Component {
    render() {
        return (
            <div className="student-preview">
                <div className="title">
                    <h4>{this.props.student.first_name}</h4>
                    <h4>{this.props.student.last_name}</h4>
                </div>
                <h4 className="break-line title" />
                <div className="student-preview-commits">
                    <h5>Time: {this.props.student.timeSpent[this.props.projectID]}</h5>
                    <h5>Commits: {this.props.student.commitCounts[this.props.projectID]}</h5>
                </div>
                <div className="student-preview-progress">
                    <div className="progress-bar">
                        <div style={{width: this.props.student.grades[this.props.projectID] + "%"}} />             
                    </div>
                    <div className="progress-text">
                        {parseInt(this.props.student.grades[this.props.projectID])}%
                    </div>
                </div>
            </div>
        );
    }
}

export default StudentPreview;
