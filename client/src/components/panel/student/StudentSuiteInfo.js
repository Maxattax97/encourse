import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {getCurrentStudent} from "../../../redux/state-peekers/student"
import {getCurrentProject, getSuiteGrades} from '../../../redux/state-peekers/projects'
import {retrieveSuitesScore} from '../../../redux/retrievals/projects'

class StudentSuiteInfo extends Component {

    componentDidMount() {
        if(this.props.student && this.props.project)
            retrieveSuitesScore(this.props.student, this.props.project)
    }

    componentDidUpdate(prevProps) {
        if(this.props.student && this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveSuitesScore(this.props.student, this.props.project)
    }

    render() {
        return (
            <Statistics stats={this.props.stats} noColumns>
                <h4 className='header'>
                    Test Suites Information
                </h4>
                <div className='h5 break-line' />
            </Statistics>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        stats: getSuiteGrades(state)
    }
}

export default connect(mapStateToProps)(StudentSuiteInfo)
