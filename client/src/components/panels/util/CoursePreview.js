import React, { Component } from 'react'
import { connect } from 'react-redux'

// import { removeCourse } from '../../../redux/actions'
// import url from '../../../server'

class CoursePreview extends Component {

    render() {
        return (
            <div className="student-preview" onClick={this.deleteCourse}>
                <div className="title">
                    <h3>{this.props.course.name}</h3>
                    <h3>{this.props.course.semester}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
    }
}

const mapDispatchToProps = (/* dispatch */) => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursePreview)
