import React, { Component } from 'react'
import PreviewCard from '../common/PreviewCard'

class SectionPreview extends Component {

    render() {
        return (
            <PreviewCard onClick={ this.props.onClick } isSelected={ this.props.isSelected }>
	            <div className="title">
		            <h4>{this.props.section.name}</h4>
		            <h4>{this.props.section.time}</h4>
	            </div>
	            <div className="h4 break-line header" />
	            <div className="preview-content">
		            <div className="stat float-height">
			            <h5>Students</h5>
			            <h5>{this.props.section.students.length}</h5>
		            </div>
		            <div className="stat float-height">
			            <h5>TAs</h5>
			            <h5>{this.props.section.teaching_assistants.length}</h5>
		            </div>
	            </div>
            </PreviewCard>
        )
    }
}

export default SectionPreview