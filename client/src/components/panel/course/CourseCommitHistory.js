import React, { Component } from 'react'
import { connect } from 'react-redux'
import CommitHistory from '../common/CommitHistory'

class CourseCommitHistory extends Component {
    render() {
        return (
            <CommitHistory />
        )
    }
}

export default connect(null, null)(CourseCommitHistory)