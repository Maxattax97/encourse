import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getStatistics } from '../../../redux/actions'
import url from '../../../server'

const defaultData = [
    {
        stat_name: 'Estimated Time Spent',
        stat_value: '5 hours'
    },
    {
        stat_name: 'Additions',
        stat_value: '103'
    },
    {
        stat_name: 'Deletions',
        stat_value: '3415'
    },
]

class StudentStatistics extends Component {

    constructor(props) {
        super(props)

        this.state = {
            stats: [
                {
                    stat_name: 'Estimated Time Spent',
                    stat_value: '5 hours'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '103'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '3415'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '`35'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '1234'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '123'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '5342'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '213'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '76'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '123'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '567'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '43'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '123'
                },
                {
                    stat_name: 'Additions',
                    stat_value: '45'
                },
                {
                    stat_name: 'Deletions',
                    stat_value: '36'
                }
            ],
            formattedData: [],
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps(nextProps) {
        if(!this.props.isFinished && nextProps.isFinished) {
            this.setState({ formattedData: this.formatApiData(nextProps.stats) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        if(props.projectID) {
            props.getStatistics(`${url}/api/statistics?projectID=${props.projectID}&userName=${props.id}`)
        }
    }

    formatApiData = (udata) => {
        if (!udata || !udata.data) {
            return defaultData
        }
        const data = udata.data
        if (!data) {
            return defaultData
        }
        const formattedData = data.slice()

        return formattedData
    }

    render() {
        return (
            <div className="student-stats-container">
                <h3 className='header'>Statistics</h3>
                <div className="h3 break-line header" />
                {
                    this.state.formattedData &&
                    this.state.formattedData.map &&
                    this.state.formattedData.map((stat)  =>
                        <div key={stat.stat_name} className="stat float-height">
                            <h5>{stat.stat_name}</h5>
                            <h5>{stat.stat_value}</h5>
                        </div>)
                }
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        stats: state.student && state.student.getStatisticsData ? state.student.getStatisticsData : [],
        isLoading: state.student ? state.student.getStatisticsIsLoading : true,
        isFinished: state.student ? state.student.getStatisticsIsFinished : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStatistics: (url, headers, body) => dispatch(getStatistics(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentStatistics)
