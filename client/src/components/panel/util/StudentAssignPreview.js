import React, { Component } from 'react'
import { Card } from '../../Helpers'

class StudentAssignPreview extends Component {

    render() {
        return (
            <Card onClick={this.props.onClick}>
                <div className="summary-preview">
                    <div className="title">
                        <h4>{this.props.student}</h4>
                    </div>
                    <div className="h4 break-line header" />
                </div>
            </Card>
        )
    }
}

export default StudentAssignPreview