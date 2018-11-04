import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassCommitHistory, updateCommitsPage, resetCommitsPage } from '../../../redux/actions/index'
import url from '../../../server'
import CommitHistory from '../common/CommitHistory'

const defaultData = []
class CourseCommitHistory extends Component {

    constructor(props) {
        super(props)

        this.state = {
            formattedData: [],
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps(nextProps) {
        if(this.props.isLoading && !nextProps.isLoading) {
            this.setState({ formattedData: this.formatApiData(nextProps.commits) })
        }
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        if(props.currentProjectId) {
            props.resetCommitsPage()
            //TODO: add correct endpoint to commented code
            //props.getClassCommitHistory(`${url}/api/commitList?&size=5&page=1&projectID=${props.currentProjectId}`)
        }     
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }
        
        const formattedData = udata.slice()

        return formattedData
    }

    scrolledToBottom = () => {
        if(!this.props.last) {
            //this.props.getClassCommitHistory(`${url}/api/commitList?&size=5&page=${this.props.page + 1}&projectID=${this.props.currentProjectId}`)
            this.props.updateCommitsPage()
        }
    }

    render() {
        return (
            <CommitHistory onPaginate={ this.scrolledToBottom } />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        commits: state.student && state.student.getClassCommitHistoryData ? state.student.getClassCommitHistoryData.content : [],
        isLoading: state.student ? state.student.getClassCommitHistoryIsLoading : false,
        page: state.student && state.student.commitsPage ? state.student.commitsPage : 1,
        last: state.student && state.student.getClassCommitHistoryData ? state.student.getClassCommitHistoryData.last : true,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassCommitHistory: (url, headers, body) => dispatch(getClassCommitHistory(url, headers, body)),
        updateCommitsPage: () => dispatch(updateCommitsPage()),
        resetCommitsPage: () => dispatch(resetCommitsPage()),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CourseCommitHistory)