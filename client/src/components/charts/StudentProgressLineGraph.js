import React, { Component } from 'react';
import { LineChart, CartesianGrid, XAxis, YAxis,
    Tooltip, Legend, Line, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import { getProgressLine } from '../../redux/actions'
import url from '../../server'

class StudentProgressLineGraph extends Component {
    constructor(props) {
        super(props);

        const defaultData = [
            {date: moment('9/10/18').valueOf(), progress: 0},
            {date: moment('9/11/18').valueOf(), progress: 20},
            {date: moment('9/12/18').valueOf(), progress: 20},
            {date: moment('9/13/18').valueOf(), progress: 30},
            {date: moment('9/14/18').valueOf(), progress: 50},
            {date: moment('9/15/18').valueOf(), progress: 60},
            {date: moment('9/16/18').valueOf(), progress: 100},
        ];

        this.state = {
            formattedData: defaultData,
        };
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps(nextProps) {
        if(!this.props.data && nextProps.data) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        props.getData(`${url}/secured/progress?projectID=${props.projectID}&userName=${props.id}`, 
        {'Authorization': `Bearer ${props.token}`})
    }


    formatApiData = (data) => {
        for (let entry of data) {
            entry.date = moment(entry.date).valueOf();
        }

        const minDate = data.reduce((min, p) => p.date < min ? p.date : min, data[0].date);
        const maxDate = data.reduce((max, p) => p.date > max ? p.date : max, data[0].date);

        const formattedData = []

        let inputIndex = 0;
        for (let m = moment(minDate); m.diff(moment(maxDate), 'days') <= 0; m.add(1, 'days')) {
            const inputEntry = data[inputIndex];
            const inputDate = inputEntry.date;

            if (m.isSame(inputDate, 'day')) {
                formattedData.push(inputEntry);
                inputIndex++;
            }
            else {
                formattedData.push({
                    date: m.valueOf(),
                    progress: inputEntry.progress,
                })
            }
        }

        return formattedData;
    }

    dateFormatter = (dateUnix) => {
        return moment(dateUnix).format('M-D')
    }

    render() {
        return (
          <div className="chart-container">
            <ResponsiveContainer width="100%" height="100%">
                <LineChart className="chart" width={730} height={500} data={this.state.formattedData}
                 margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                    <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Student Progress Over Time</text>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" type="number" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                        <Label value="Date" position="bottom" />
                    </XAxis>
                    <YAxis>
                        <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                            % Completion
                        </Label>
                    </YAxis>
                    <Tooltip labelFormatter={this.dateFormatter}/>
                    <Legend verticalAlign="top"/>
                    <Line type="monotone" dataKey="progress" stroke="#8884d8" />
                </LineChart>
            </ResponsiveContainer>
          </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        data: state.student && state.student.getProgressLineData ? state.student.getProgressLineData : null,
        isLoading: state.student ? state.student.getProgressLineIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getProgressLine(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentProgressLineGraph)
