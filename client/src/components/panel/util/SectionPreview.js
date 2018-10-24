import React, { Component } from 'react'
import { Card } from '../../Helpers'

class SectionPreview extends Component {

    render() {
        return (
            <Card onClick={this.props.onClick}>
                <div className="summary-preview">
                    <div className="title">
                        <h4>{this.props.section.name}</h4>
                        <h4>{this.props.section.time}</h4>
                    </div>
                    <div className="h4 break-line header" />
                    <div className="preview-content">
                        <div className="stat float-height">
                            <h5>Students</h5>
                            <h5>{this.props.section.students}</h5>
                        </div>
                        <div className="stat float-height">
                            <h5>TAs</h5>
                            <h5>{this.props.section.teaching_assistants}</h5>
                        </div>
                    </div>
                </div>
            </Card>
        )
    }
}

export default SectionPreview