import React, { Component } from 'react'
import { connect } from 'react-redux'
import CommitHistory from '../common/CommitHistory'

class CourseCommitHistory extends Component {

    scrolledToBottom = () => {
        console.log('bottom')
    }

    render() {
        return (
            <CommitHistory onPaginate={ this.scrolledToBottom } />
        )
    }
}

export default connect(null, null)(CourseCommitHistory)