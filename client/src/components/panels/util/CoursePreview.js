import React, { Component } from 'react';
import { connect } from 'react-redux'

import { removeCourse } from '../../../redux/actions'
import url from '../../../server'

class CoursePreview extends Component {

    deleteCourse = () => {
        //TODO!: add endpoint
        this.props.removeAccount(`${url}/api/delete/user`,
        {'Authorization': `Bearer ${this.props.token}`})
    }

    render() {
        return (
            <div className="student-preview" onClick={this.deleteCourse}>
                <div className="title">
                    <h3>{this.props.course.name}</h3>
                    <h3>{this.props.course.semester}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        removeCourse: (url, headers, body) => dispatch(removeCourse(url, headers, body)),
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(CoursePreview);

