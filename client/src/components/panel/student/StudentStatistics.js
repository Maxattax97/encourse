import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {getCurrentStudent, getStudentStatistics} from "../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../redux/state-peekers/project"
import {retrieveStudentStats} from "../../../redux/retrievals/student"

class StudentStatistics extends Component {

	componentWillMount() {
		if(this.props.student && this.props.project)
			retrieveStudentStats(this.props.student, this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveStudentStats(this.props.project)
	}

    render() {
        return (
            <Statistics stats={this.props.stats} />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        stats: getStudentStatistics(state)
    }
}

export default connect(mapStateToProps)(StudentStatistics)
