import React, { Component } from 'react'
import { connect } from 'react-redux'

import {
    getStudent,
} from '../../redux/actions/index'
import {retrieveStudent} from "../../redux/retrievals/student"
import {getCurrentCommit, getCurrentStudent} from "../../redux/state-peekers/student"
import {getCurrentCourseId, getCurrentSemesterId} from "../../redux/state-peekers/course"
import BackNavigation from '../navigation/BackNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {history} from '../../redux/store'
import {clearCommit} from "../../redux/actions/student"
import StudentCommitDiff from "./student-commit-diff/StudentCommitDiff"


class StudentCommitDiffPanel extends Component {

    componentDidMount = () => {
        if(!this.props.commit)
            history.push(`/${this.props.currentCourseId}/course`)
        retrieveStudent(this.props.student.studentID)
    }

	componentWillUnmount() {
		this.props.clearCommit()
	}

    render() {
        if(!this.props.commit)
            return null;

        const action_names = [
            'Student Page',
            'Student Dishonesty Page',
            'Course Page'
        ]

        const actions = [
            () => {
                history.push(`/${this.props.course}/student/${this.props.student.studentID}`)
            },
            () => {
                history.push(`/${this.props.course}/student-dishonesty/${this.props.student.id}`)
            },
            () => {
                history.push(`/${this.props.course}/course`)
            }
        ]

        return (
            <div className="panel-student">

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className="panel-center-content">
                    <div className='panel-commit-content'>
                        <h1 className='header'>
                            {
                                this.props.student ? `${this.props.student.firstName} ${this.props.student.lastName}, ${this.props.commit.hash}` : ''
                            }
                        </h1>
                        <div className="h1 break-line header" />

                        <StudentCommitDiff/>

                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        commit: getCurrentCommit(state),
        course: getCurrentCourseId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        clearCommit: () => dispatch(clearCommit())
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentCommitDiffPanel)
