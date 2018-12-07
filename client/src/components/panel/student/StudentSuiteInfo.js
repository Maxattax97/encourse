import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {getCurrentStudent} from "../../../redux/state-peekers/student"
import {getCurrentProject, getTestSuites} from '../../../redux/state-peekers/projects'
import {retrieveTestSuites} from '../../../redux/retrievals/projects'

class StudentSuiteInfo extends Component {

    componentDidMount() {
        if(this.props.student && this.props.project)
            retrieveTestSuites(this.props.student, this.props.project)
    }

    componentDidUpdate(prevProps) {
        if(this.props.student && this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveTestSuites(this.props.student, this.props.project)
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
        stats: getTestSuites(state)
    }
}

export default connect(mapStateToProps)(StudentSuiteInfo)
