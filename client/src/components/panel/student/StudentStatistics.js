import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getStatistics } from '../../../redux/actions/index'
import url from '../../../server'
import {LoadingIcon} from "../../Helpers"
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
            props.getStatistics(`${url}/api/statistics?projectID=${props.currentProjectId}&userName=${props.currentStudent.id}`)
        }
    }

    formatApiData = (udata) => {
        if (!udata) {
            return null
        }
        udata = udata[0]
        if (!udata || !udata.data) {
            return null
        }
        const data = udata.data
        if (!data) {
            return null
        }

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
        stats: state.student && state.student.getStatisticsData ? state.student.getStatisticsData : [],
        isLoading: state.student ? state.student.getStatisticsIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStatistics: (url, headers, body) => dispatch(getStatistics(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentStatistics)
