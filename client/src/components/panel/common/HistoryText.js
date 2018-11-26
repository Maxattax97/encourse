import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassProjects } from '../../../redux/actions'

class HistoryText extends Component {
    componentDidMount = () => {
        if(this.props.projects.length === 0) {
            //TODO: remove classid and semester hardcoding
            // this.props.getClassProjects(`${url}/api/projectsData?courseID=cs252&semester=Fall2018`)
        }
    }

    render() {
        return (
            <div className='top-nav'>
                <div>
                    <h4>Last Sync: {
                        this.props.projects && this.props.projects.length > 0 && this.props.projects[this.props.currentProjectIndex] ?
                            this.props.projects[this.props.currentProjectIndex].last_sync
                            : null
                    }
                    </h4>
                </div>
                <div>
                    <h4>Last Test Ran: {
                        this.props.projects && this.props.projects.length > 0  && this.props.projects[this.props.currentProjectIndex] ?
                            this.props.projects[this.props.currentProjectIndex].last_test
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
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        currentProjectIndex: state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassProjects: (url, headers) => dispatch(getClassProjects(url, headers)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(HistoryText)
