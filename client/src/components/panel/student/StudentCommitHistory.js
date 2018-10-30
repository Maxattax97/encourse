import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card} from '../../Helpers'

import { getCommitHistory } from '../../../redux/actions/index'
import url from '../../../server'

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
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        if(props.currentProjectId) {
            props.getCommitHistory(`${url}/api/commitList?projectID=${props.currentProjectId}&userName=${props.currentStudent.id}`)
        }     
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }
        const data = udata.data
        const formattedData = data.slice()

        return formattedData
    }

    render() {
        return (
            <div className="commits-container side-nav-right">
                <Card>
                    <div className='commits'>
                        <h3 className='header'>History</h3>
                        <div className="h3 break-line header" />
                        { !this.props.isLoading
                            ? <div className="float-height overflow">
                                {
                                    this.state.formattedData &&
                                this.state.formattedData.map((commit) =>
                                    <Card key={commit.date}>
                                        <div className="student-commit-container">
                                            <h5>
                                                { commit.date }
                                            </h5>
                                            <h5>
                                                Main Changes
                                            </h5>
                                            <ul>
                                                {
                                                    commit.files.map((file) =>
                                                        <li key={file}>
                                                            { file }
                                                        </li>
                                                    )
                                                }
                                            </ul>
                                        </div>
                                    </Card>
                                )
                                }
                            </div>
                            : <div>{/* TODO: add spinner */}Loading</div>
                        }
                    </div>
                </Card>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        commits: state.student && state.student.getCommitHistoryData ? state.student.getCommitHistoryData : [],
        isLoading: state.student ? state.student.getCommitHistoryIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getCommitHistory: (url, headers, body) => dispatch(getCommitHistory(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentCommitHistory)