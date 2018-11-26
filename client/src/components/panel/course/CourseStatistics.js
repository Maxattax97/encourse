import React, { Component } from 'react'
import { connect } from 'react-redux'

import Statistics from "../common/Statistics"
import {retrieveStats} from "../../../redux/retrievals/course"
import {getStats} from "../../../redux/state-peekers/course"
import {getCurrentProject} from "../../../redux/state-peekers/project"

class StudentStatistics extends Component {

    componentWillReceiveProps(nextProps) {
        if(nextProps.project && (!(this.props.project) || this.props.project.index !== nextProps.project.index))
	        retrieveStats(nextProps.project)
    }

    render() {
        return (
            <Statistics isLoading={this.props.stats.loading} values={this.props.stats.data} />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        project: getCurrentProject(state),
        stats: getStats(state)
    }
}

export default connect(mapStateToProps, null)(StudentStatistics)
