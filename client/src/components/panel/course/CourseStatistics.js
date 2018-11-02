import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassStatistics } from '../../../redux/actions/index'
import url from '../../../server'
import {LoadingIcon, Card} from '../../Helpers'

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
            props.getClassStatistics(`${url}/api/statistics?projectID=${props.currentProjectId}`)
        }
    }

    formatApiData = (udata) => {
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
            <div className='summary'>
                <div className='summary-container'>
                    <div className='float-height cols-2'>
                        <Card>
                            {
                                !this.props.isLoading && this.state.formattedData && this.state.formattedData.map ?
                                    this.state.formattedData.map((stat)  =>
                                        <div key={stat.stat_name} className="stat float-height">
                                            <h5>{stat.stat_name}</h5>
                                            <h5>{stat.stat_value}</h5>
                                        </div>
                                    )
                                    :
                                    <div className='loading'>
                                        <LoadingIcon/>
                                    </div>
                            }
                        </Card>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
        stats: state.student && state.student.getClassStatisticsData ? state.student.getClassStatisticsData : [],
        isLoading: state.student ? state.student.getStatisticsIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getClassStatistics: (url, headers, body) => dispatch(getClassStatistics(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentStatistics)
