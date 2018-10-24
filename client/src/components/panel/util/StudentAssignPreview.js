import React, { Component } from 'react'

class StudentAssignPreview extends Component {

    render() {
        return (
            <div className="student-preview">
                <div className="title">
                    <h4>{this.props.student}</h4>
                </div>
                <div className="h4 break-line header" />
            </div>
        )
    }
}

export default StudentAssignPreview