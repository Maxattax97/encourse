
import React, { Component } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Label, ResponsiveContainer } from 'recharts';
import moment from 'moment';

const defaultData = [
    {date: moment('2018-09-16').valueOf(), count: 8},
    {date: moment('2018-09-17').valueOf(), count: 13},
    {date: moment('2018-09-18').valueOf(), count: 14},
    {date: moment('2018-09-19').valueOf(), count: 3},
    {date: moment('2018-09-20').valueOf(), count: 4},
    {date: moment('2018-09-21').valueOf(), count: 6},
    {date: moment('2018-09-22').valueOf(), count: 92},
    {date: moment('2018-09-23').valueOf(), count: 104},
    {date: moment('2018-09-24').valueOf(), count: 2},
    {date: moment('2018-09-25').valueOf(), count: 0},
    {date: moment('2018-09-26').valueOf(), count: 0},
    {date: moment('2018-09-27').valueOf(), count: 0},
];

function dateFormatter(date) {
    const m = moment(date);
    return m.format('M-D')
}

class CommitHistoryHistogram extends Component {
    render() {
        return (
            <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={this.props.data || defaultData} margin={{top: 40, right: 30, left: 20, bottom: 30}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Commit Frequency</text>
                        <CartesianGrid/>
                        <XAxis dataKey="date" tickFormatter={dateFormatter}>
                            <Label offset={-15} position="insideBottom">
                                Commits
                            </Label>
                        </XAxis>
                        <YAxis>
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                Date
                            </Label>
                        </YAxis>
                        <Tooltip labelFormatter={dateFormatter} animationDuration={500}/>
                        <Bar dataKey="count" fill="#8884d8"/>
                    </BarChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

export default CommitHistoryHistogram;
