import React, { Component } from 'react'
import { connect } from 'react-redux'

// import { removeCourse } from '../../../redux/actions'
// import url from '../../../server'

class CoursePreview extends Component {

    render() {
        return (
            <div className="student-preview" onClick={this.deleteCourse}>
                <div className="title">
                    <h3>{this.props.course.courseID}</h3>
                    <h3>{this.props.course.courseTitle}</h3>
                    <h3>{this.props.course.sectionType}</h3>
                    <h3>{this.props.course.semester}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        )
    }
}

const mapDispatchToProps = (/* dispatch */) => {
    return {
    }
}

export default connect(null, mapDispatchToProps)(CoursePreview)
