
import React, { Component } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Label, ResponsiveContainer } from 'recharts';
import { connect } from 'react-redux'

import { getCommitFrequency } from '../../redux/actions'

const defaultData = [
    {date: new Date('2018-09-16T00:00:00'), count: 8},
    {date: new Date('2018-09-17T00:00:00'), count: 13},
    {date: new Date('2018-09-18T00:00:00'), count: 14},
    {date: new Date('2018-09-19T00:00:00'), count: 3},
    {date: new Date('2018-09-20T00:00:00'), count: 4},
    {date: new Date('2018-09-21T00:00:00'), count: 6},
    {date: new Date('2018-09-22T00:00:00'), count: 92},
    {date: new Date('2018-09-23T00:00:00'), count: 104},
    {date: new Date('2018-09-24T00:00:00'), count: 2},
    {date: new Date('2018-09-25T00:00:00'), count: 0},
    {date: new Date('2018-09-26T00:00:00'), count: 0},
    {date: new Date('2018-09-27T00:00:00'), count: 0},
];

for (let item of defaultData) {
    let m = item.date;
    item.dateStr = (m.getUTCMonth()+1) + '/' + m.getUTCDate();
}

class CommitHistoryHistogram extends Component {

    componentDidMount = () => {
        this.props.getData(/*TODO: add endpoint */)
    }

    render() {
        return (
            <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={this.props.data || defaultData} margin={{top: 40, right: 30, left: 20, bottom: 30}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Commit Frequency</text>
                        <CartesianGrid/>
                        <XAxis dataKey="dateStr">
                            <Label offset={-15} position="insideBottom">
                                Commits
                            </Label>
                        </XAxis>
                        <YAxis>
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Date
                            </Label>
                        </YAxis>
                        <Tooltip/>
                        <Bar dataKey="count" fill="#8884d8"/>
                    </BarChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return { 
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        data: state.student && state.student.getCommitFrequencyData ? state.student.getCommitFrequencyData : null,
        isLoading: state.student ? state.student.getCommitFrequencyIsLoading : false,
    }
  }
  
  const mapDispatchToProps = (dispatch) => {
      return {
          getData: (url, headers, body) => dispatch(getCommitFrequency(url, headers, body))
      }
  }
  
  export default connect(mapStateToProps, mapDispatchToProps)(CommitHistoryHistogram)