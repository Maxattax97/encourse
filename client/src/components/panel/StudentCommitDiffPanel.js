import React, { Component } from 'react'
import { connect } from 'react-redux'

import {
    getStudent,
} from '../../redux/actions/index'
import {retrieveStudent} from "../../redux/retrievals/student"
import {getCurrentStudent} from "../../redux/state-peekers/student"
import {getCurrentCourseId, getCurrentSemesterId} from "../../redux/state-peekers/course"
import BackNavigation from '../navigation/BackNavigation'


class StudentPanel extends Component {

    componentDidMount = () => {
        if(!this.props.currentStudent) {
            retrieveStudent({id: this.props.match.params.id}, this.props.currentCourseId, this.props.currentSemesterId)
        }
    }

    render() {


        return (
            <div className="panel-student">

                <div className='panel-left-nav'>
                    <BackNavigation/>
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
