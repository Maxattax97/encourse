import React, { Component } from 'react'
import { Card } from '../../Helpers'
import connect from 'react-redux/es/connect/connect'

class StudentAssignPreview extends Component {

    render() {
        return (
            <Card onClick={this.props.onClick} className={this.props.isSelected ? 'action selected' : 'action'}>
                <div className="summary-preview">
                    <div className="title">
                        <h4>{this.props.student.first_name}</h4>
                        <h4>{this.props.student.last_name}</h4>
                    </div>
                    <div className="h4 break-line header" />
                    <div className="preview-content">
                        <h5>Sections : { this.props.student.sections.map((id, index) => (this.props.sections.find(section => section.id === id).name) + (index === this.props.student.sections.length - 1 ? '' : ', ')) }</h5>
                        <h5>TAs : { this.props.student.teaching_assistants.map((id, index) => (id + (index === this.props.student.teaching_assistants.length - 1 ? '' : ', '))) }</h5>
                    </div>
                </div>
            </Card>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        sections: state.course && state.course.getSectionsData ? state.course.getSectionsData : [],
        teaching_assistants: state.teachingAssistant && state.teachingAssistant.getTeachingAssistants ? state.teachingAssistant.getTeachingAssistants : []
    }
}

const mapDispatchToProps = () => {
    return {

    }
}


export default connect(mapStateToProps, mapDispatchToProps)(StudentAssignPreview)