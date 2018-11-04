import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getCommitHistory, updateCommitsPage, resetCommitsPage } from '../../../redux/actions/index'
import url from '../../../server'
import CommitHistory from '../common/CommitHistory'

const defaultData = [{'date': '2018-08-25', 'files': ['myMalloc.c', 'printing.c', 'testing.c'], 'time_spent': '50 minutes', 'additions': 2060, 'deletions': 2, 'commit_count': 3}, {'date': '2018-08-30', 'files': ['myMalloc.c', 'myMalloc.h', 'Makefile'], 'time_spent': '3 hours', 'additions': 159, 'deletions': 100, 'commit_count': 25}, {'date': '2018-09-04', 'files': ['myMalloc.c'], 'time_spent': '4 hours', 'additions': 147, 'deletions': 45, 'commit_count': 15}, {'date': '2018-09-05', 'files': ['myMalloc.c', 'tests/Makefile'], 'time_spent': '6 hours', 'additions': 279, 'deletions': 132, 'commit_count': 28}, {'date': '2018-09-06', 'files': ['.local.git.out', 'myMalloc.c', 'examples/composite_ex'], 'time_spent': '3 hours', 'additions': 1072, 'deletions': 152, 'commit_count': 29}]

class StudentCommitHistory extends Component {

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
            props.getCommitHistory(`${url}/api/commitList?&size=5&page=1&projectID=${props.currentProjectId}&userName=${props.currentStudent.id}`)
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
            this.props.getCommitHistory(`${url}/api/commitList?&size=5&page=${this.props.page + 1}&projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            this.props.updateCommitsPage()
        }
    }

    render() {
        return (
            <CommitHistory isLoading={ this.props.isLoading } data={ this.state.formattedData } onPaginate={ this.scrolledToBottom }/>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        commits: state.student && state.student.getCommitHistoryData ? state.student.getCommitHistoryData.content : [],
        isLoading: state.student ? state.student.getCommitHistoryIsLoading : false,
        page: state.student && state.student.commitsPage ? state.student.commitsPage : 1,
        last: state.student && state.student.getCommitHistoryData ? state.student.getCommitHistoryData.last : true,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getCommitHistory: (url, headers, body) => dispatch(getCommitHistory(url, headers, body)),
        updateCommitsPage: () => dispatch(updateCommitsPage()),
        resetCommitsPage: () => dispatch(resetCommitsPage()),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentCommitHistory)
