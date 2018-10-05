import React, { Component } from 'react';

class CoursePreview extends Component {
    render() {
        return (
            <div className="student-preview">
                <div className="title">
                    <h3>{this.props.course.name}</h3>
                    <h3>{this.props.course.semester}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        );
    }
}

export default CoursePreview;
