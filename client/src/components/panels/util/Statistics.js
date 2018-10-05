import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getStatistics } from '../../../redux/actions/index'
import url from '../../../server'

const defaultData = [
                {
                    stat_name: "Estimated Time Spent",
                    stat_value: "5 hours"
                },
                {
                    stat_name: "Additions",
                    stat_value: "103"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "3415"
                },
                {
                    stat_name: "Additions",
                    stat_value: "`35"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "1234"
                },
                {
                    stat_name: "Additions",
                    stat_value: "123"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "5342"
                },
                {
                    stat_name: "Additions",
                    stat_value: "213"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "76"
                },
                {
                    stat_name: "Additions",
                    stat_value: "123"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "567"
                },
                {
                    stat_name: "Additions",
                    stat_value: "43"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "123"
                },
                {
                    stat_name: "Additions",
                    stat_value: "45"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "36"
                }
            ]

class Statistics extends Component {

    constructor(props) {
        super(props);

        this.state = {
            stats: [
                {
                    stat_name: "Estimated Time Spent",
                    stat_value: "5 hours"
                },
                {
                    stat_name: "Additions",
                    stat_value: "103"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "3415"
                },
                {
                    stat_name: "Additions",
                    stat_value: "`35"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "1234"
                },
                {
                    stat_name: "Additions",
                    stat_value: "123"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "5342"
                },
                {
                    stat_name: "Additions",
                    stat_value: "213"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "76"
                },
                {
                    stat_name: "Additions",
                    stat_value: "123"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "567"
                },
                {
                    stat_name: "Additions",
                    stat_value: "43"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "123"
                },
                {
                    stat_name: "Additions",
                    stat_value: "45"
                },
                {
                    stat_name: "Deletions",
                    stat_value: "36"
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
        props.getStatistics(`${url}/secured/statistics?projectID=${props.projectID}&userName=${props.id}`,
        {'Authorization': `Bearer ${props.token}`})
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }
        const data = udata.data;
        const formattedData = data.slice();

        return formattedData;
    }

    render() {
        return (
            <div className="student-stats-container">
                <div className="title">
                    <h3>Statistics</h3>
                </div>
                <h3 className="break-line title" />
                {
                    this.state.formattedData &&
                    this.state.formattedData.map &&
                    this.state.formattedData.map((stat, index)  =>
                        <div className="student-stat">
                            <div className="student-stat-content">
                                <h5>{stat.stat_name}</h5>
                                <h5>{stat.stat_value}</h5>
                            </div>
                            {
                                index % 2 === 1 && index !== this.state.stats.length - 1 ?
                                    <h5 className="break-line" /> : null
                            }
                        </div>)
                }
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
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

  export default connect(mapStateToProps, mapDispatchToProps)(Statistics)
