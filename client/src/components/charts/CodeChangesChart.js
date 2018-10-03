import React, { Component } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Label, Legend, ResponsiveContainer } from 'recharts';
import moment from 'moment'
import { connect } from 'react-redux'

import { getCodeFrequency } from '../../redux/actions'
import url from '../../server'

const defaultData = [
    {date: moment('9/9/2018').valueOf(), additions: 200, deletions: -0},
    {date: moment('9/10/2018').valueOf(), additions: 32, deletions: -0},
    {date: moment('9/11/2018').valueOf(), additions: 100, deletions: -0},
    {date: moment('9/12/2018').valueOf(), additions: 765, deletions: -0},
    {date: moment('9/13/2018').valueOf(), additions: 20, deletions: -50},
    {date: moment('9/14/2018').valueOf(), additions: 0, deletions: -10},
    {date: moment('9/15/2018').valueOf(), additions: 10, deletions: -100},
    {date: moment('9/16/2018').valueOf(), additions: 84, deletions: -327},
    {date: moment('9/17/2018').valueOf(), additions: 284, deletions: -400},
    {date: moment('9/18/2018').valueOf(), additions: 102, deletions: -90},
];

class CodeChangesChart extends Component {
    constructor(props) {
        super(props);

        this.state = {
            formattedData: defaultData,
        }
    }
  
    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if(!this.props.data && nextProps.data) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        props.getData(`${url}/secured/diffs?projectID=${props.projectID}&userName=${props.id}`, 
        {'Authorization': `Bearer ${props.token}`})
    }

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }

    formatApiData = (data) => {
        for (let entry of data) {
            entry.date = moment(entry.date).valueOf();
            entry.deletions = -entry.deletions;
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
                    additions: 0,
                    deletions: 0,
                })
            }
        }

        return formattedData;
    }

    render() {
        return (
            <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={this.state.formattedData} margin={{top: 40, right: 30, left: 0, bottom: 25}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Code Frequency</text>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <XAxis type="number" dataKey="date" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                            <Label position="insideBottom" offset={-15} value="Date"/>
                        </XAxis>
                        <YAxis/>
                        <Tooltip labelFormatter={this.dateFormatter}/>
                        <Area type="monotone" dataKey="additions" stroke="none" fill="green" />
                        <Area type="monotone" dataKey="deletions" stroke="none" fill="red" />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        data: state.student && state.student.getCodeFrequencyData ? state.student.getCodeFrequencyData : null,
        isLoading: state.student ? state.student.getCodeFrequencyIsLoading : false,
    }
  }

  const mapDispatchToProps = (dispatch) => {
      return {
          getData: (url, headers, body) => dispatch(getCodeFrequency(url, headers, body))
      }
  }

  export default connect(mapStateToProps, mapDispatchToProps)(CodeChangesChart)
