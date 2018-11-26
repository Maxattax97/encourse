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

    componentWillReceiveProps(nextProps) {
	    if(nextProps.student && nextProps.project && (!(this.props.project) || !(this.props.student) || this.props.project.index !== nextProps.project.index))
	        retrieveStudentStats(nextProps.student, nextProps.project)
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
