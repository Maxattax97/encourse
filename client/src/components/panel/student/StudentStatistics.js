import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getStatistics } from '../../../redux/actions/index'
import url from '../../../server'
import Statistics from "../common/Statistics"
import {getCurrentStudent, getStudentStatistics} from "../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../redux/state-peekers/project"

class StudentStatistics extends Component {

    componentWillReceiveProps(nextProps) {
	    if(nextProps.project && (!(this.props.project) || this.props.project.index !== nextProps.project.index))
	        this.fetch(nextProps)
    }

    fetch = (props) => {
            props.getStatistics(`${url}/api/statistics?projectID=${props.project.id}&userName=${props.student.id}`)
    }

    render() {
        return (
            <Statistics isLoading={this.props.stats.loading} values={this.props.stats.data} />
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

const mapDispatchToProps = (dispatch) => {
    return {
        getStatistics: (url, headers, body) => dispatch(getStatistics(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentStatistics)
