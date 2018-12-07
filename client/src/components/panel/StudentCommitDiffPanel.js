import React, { Component } from 'react'
import { connect } from 'react-redux'

import {
    getStudent,
} from '../../redux/actions/index'
import {retrieveStudent} from "../../redux/retrievals/student"
import {getCurrentStudent} from "../../redux/state-peekers/student"
import {getCurrentCourseId, getCurrentSemesterId} from "../../redux/state-peekers/course"
import BackNavigation from '../navigation/BackNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {history} from '../../redux/store'


class StudentPanel extends Component {

    componentDidMount = () => {
        if(!this.props.currentStudent)
            retrieveStudent({id: this.props.match.params.studentID}, this.props.currentCourseId, this.props.currentSemesterId)
    }

    render() {
        const action_names = [
            'View Current Task',
            'Student Page',
            'Academic Dishonesty Report'
        ]

        let studentDishonestyRedirect = () => { history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/student-dishonesty/${this.props.student.id}`)}

        const actions = [
            () => {
                this.props.setModalState(2)
            },
            () => {
                history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/student/${this.props.student.id}`)
            },
            studentDishonestyRedirect
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
                                this.props.student ? `${this.props.student.first_name} ${this.props.student.last_name}` : ''
                            }
                        </h1>
                        <div className="h1 break-line header" />



                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentPanel)