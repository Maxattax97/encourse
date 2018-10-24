
import React, { Component } from 'react'
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Tooltip, Label, Brush, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import { getProgressVelocity } from '../../redux/actions'
import url from '../../server'

const defaultData = [
    {date: moment('2018-09-16').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-17').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-18').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-19').valueOf(), progress: 0, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-20').valueOf(), progress: 0, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-21').valueOf(), progress: 10, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-22').valueOf(), progress: 0, timeSpent: 5, commitCount: 120},
    {date: moment('2018-09-23').valueOf(), progress: 0, timeSpent: 1, commitCount: 30},
    {date: moment('2018-09-24').valueOf(), progress: 20, timeSpent: 2, commitCount: 100},
    {date: moment('2018-09-25').valueOf(), progress: 10, timeSpent: 2, commitCount: 50},
    {date: moment('2018-09-26').valueOf(), progress: 50, timeSpent: 7, commitCount: 200},
    {date: moment('2018-09-27').valueOf(), progress: 10, timeSpent: 3, commitCount: 50},
]

class ProgressPerTime extends Component {

    constructor(props) {
        super(props)

        this.state = {
            formattedData: defaultData,
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if(!this.props.isFinished && nextProps.isFinished) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        // props.getData(`${url}/api/commitCount?projectID=${props.projectID}&userName=${props.id}`,
        //     {'Authorization': `Bearer ${props.token}`})
    }

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }
        let data = udata.data

        if (!data || data.length === 0) {
            return defaultData
        }

        for (let entry of data) {
            entry.date = moment(entry.date).valueOf()
        }

        return data
    }

    render() {
        return (
            <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <ScatterChart data={this.state.formattedData} margin={{top: 40, right: 30, left: 20, bottom: 30}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Progress per Time</text>
                        <CartesianGrid/>
                        <XAxis dataKey="timeSpent" type="number">
                            <Label offset={-15} position="insideBottom">
                                Estimated Time worked
                            </Label>
                        </XAxis>
                        <YAxis dataKey="progress" type="number">
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Progress
                            </Label>
                        </YAxis>
                        <Tooltip labelFormatter={this.dateFormatter} animationDuration={500}/>
                        <Scatter type="number" fill="#8884d8"/>
                    </ScatterChart>
                </ResponsiveContainer>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        data: state.student && state.student.getCommitFrequencyData ? state.student.getCommitFrequencyData : null,
        isLoading: state.student ? state.student.getCommitFrequencyIsLoading : false,
        isFinished: state.student ? state.student.getCommitFrequencyIsFinished : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getProgressVelocity(url, headers, body)),
    }
}

export { ProgressPerTime }
export default connect(mapStateToProps, mapDispatchToProps)(ProgressPerTime)
