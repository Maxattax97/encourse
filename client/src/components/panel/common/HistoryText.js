import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassProjects } from '../../../redux/actions'
import {getCurrentProject} from '../../../redux/state-peekers/projects'

class HistoryText extends Component {

    render() {
        return (
            <div className='top-nav'>
                <div title='The last time that the server moved the git project files from data.cs.purdue.edu to vm2'>
                    <h4>Last Sync: {
                        this.props.project ?
                            this.props.project.last_sync
                            : null
                    }
                    </h4>
                </div>
                <div title='The last time the server ran the testall scripts on each project'>
                    <h4>Last Test Ran: {
                        this.props.project ?
                            this.props.project.last_test
                            : null
                    }
                    </h4>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        project: getCurrentProject(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(HistoryText)
