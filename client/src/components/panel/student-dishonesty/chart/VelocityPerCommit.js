
import React, { Component } from 'react'
import {
    ScatterChart,
    Scatter,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Label,
    ResponsiveContainer,
    Legend,
    Line, LineChart
} from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import { getProgressPerCommit } from '../../../../redux/actions/index'
import { getCurrentStudent } from '../../../../redux/state-peekers/student'
import { getCurrentProject } from '../../../../redux/state-peekers/projects'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const defaultData = [
    {date: moment('2018-09-16').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-17').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-18').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-19').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-20').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-21').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-22').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-23').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-24').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-25').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-26').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
    {date: moment('2018-09-27').valueOf(), progress: 0, timeSpent: 0, commitCount: 0},
]

class VelocityPerCommit extends Component {

    constructor(props) {
        super(props)

        this.state = {
            formattedData: [],
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentDidUpdate = (prevProps) => {
        if(prevProps.isLoading && !this.props.isLoading) {
            const data = this.formatApiData(this.props.data)

            if(data)
                this.setState({ formattedData: data })
        }

        if (this.props.project.id !== prevProps.project.id)
            this.fetch(this.props)
    }

    fetch = (props) => {
        if(props.project && props.student) {
            props.getData(`${url}/api/velocity?projectID=${props.project.id}&userName=${props.student.id}`)
        }   
    }

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }
    
    customDateFormatter = (value, name, props) => {
        if (name === 'date') {
            return moment(value).format('M-D')
        }
        return value
    }

    formatApiData = (udata) => {
        if (!udata) {
            return defaultData
        }
        let data = udata

        if (!data || data.length === 0) {
            return defaultData
        }

        for (let entry of data) {
            entry.date = moment(entry.date).valueOf()
            entry.commitCount = entry.commit_count
            // progress per commit
            entry.ppc = entry.commitCount > 0 ? entry.progress / entry.commitCount : 0
        }

        return data
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div
                    className="chart-container"
                    title="Amount of progress made per commit"
                >
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart className="chart" width={730} height={500} data={this.state.formattedData}
                                   margin={{ top: 20, right: 35, left: 20, bottom: 20 }} syncId="date">
                            <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Student Progress Over Time</text>
                            <XAxis dataKey="date" type="number" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                                <Label value="Date" position="bottom" />
                            </XAxis>
                            <YAxis domain={[0, 100]}>
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                    % Completion
                                </Label>
                            </YAxis>
                            <Tooltip labelFormatter={this.dateFormatter}/>
                            <Legend verticalAlign="top"/>
                            <Line type="monotone" dataKey="progress" stroke="#0057A7CC" />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
                :
                <div className='chart-container loading'>
                    <LoadingIcon/>
                </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        data: state.student && state.student.getProgressPerCommitData ? state.student.getProgressPerCommitData : null,
        isLoading: state.student ? state.student.getProgressPerCommitIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getProgressPerCommit(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(VelocityPerCommit)
