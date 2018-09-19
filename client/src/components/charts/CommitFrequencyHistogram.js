
import React, { Component } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const data = [
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

for (let item of data) {
    let m = item.date;
    item.dateStr = (m.getUTCMonth()+1) + '/' + m.getUTCDate();
}

class CommitHistoryHistogram extends Component {
    render() {
        return (
            <div classname="chart-container">
                <BarChart width={600} height={300} data={data} margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                    <CartesianGrid/>
                    <XAxis dataKey="dateStr"/>
                    <YAxis/>
                    <Tooltip/>
                    <Bar dataKey="count" fill="#8884d8"/>
                </BarChart>
            </div>
        );
    }
}

export default CommitHistoryHistogram;
