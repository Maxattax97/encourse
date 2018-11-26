import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassStatistics } from '../../../redux/actions/index'
import url from '../../../server'
import Statistics from "../common/Statistics"

class StudentStatistics extends Component {

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
            const data = this.formatApiData(nextProps.stats)
            if(data)
                this.setState({ formattedData: data })
        }
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        if(props.currentProjectId) {
            props.getClassStatistics(`${url}/api/classStatistics?projectID=${props.currentProjectId}`)
        }
    }

    formatApiData = (udata) => {
        if (!udata || !udata.data) {
            return null
        }

        const data = udata.data

        data.sort((d1, d2) => d1.index - d2.index)

        return data.slice()
    }

    render() {
        return (
            <Statistics isLoading={this.props.isLoading} values={this.state.formattedData} />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        stats: state.course && state.course.getClassStatisticsData ? state.course.getClassStatisticsData : [],
        isLoading: state.course ? state.course.getClassStatisticsIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassStatistics: (url, headers, body) => dispatch(getClassStatistics(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentStatistics)
