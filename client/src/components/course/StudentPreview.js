import React, { Component } from 'react';

class StudentPreview extends Component {
    render() {
        return (
            <div className="student-preview">
                <div className="title">
                    <h4>{this.props.info.first_name}</h4>
                    <h4>{this.props.info.last_name}</h4>
                    <h4 className="break-line title" />
                </div>
                <div className="student-preview-commits">
                </div>
                <div className="student-preview-progress">
                    <div className="student-preview-progress-bar" />
                </div>
            </div>
        );
    }
}

export default StudentPreview;
