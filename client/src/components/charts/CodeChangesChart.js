import React, { Component } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';


const defaultData = [
    {date: new Date('9/9/2018'), additions: 200, deletions: -0},
    {date: new Date('9/10/2018'), additions: 32, deletions: -0},
    {date: new Date('9/11/2018'), additions: 100, deletions: -0},
    {date: new Date('9/12/2018'), additions: 765, deletions: -0},
    {date: new Date('9/13/2018'), additions: 20, deletions: -50},
    {date: new Date('9/14/2018'), additions: 0, deletions: -10},
    {date: new Date('9/15/2018'), additions: 10, deletions: -100},
    {date: new Date('9/16/2018'), additions: 84, deletions: -327},
    {date: new Date('9/17/2018'), additions: 284, deletions: -400},
    {date: new Date('9/18/2018'), additions: 102, deletions: -90},
];

for (let item of defaultData) {
    let m = item.date;
    item.dateStr = (m.getUTCMonth()+1) + '/' + m.getUTCDate();
}

class CodeChangesChart extends Component {
    render() {
        const {
            width = 600,
            height = 300,
            data = defaultData
        } = this.props;

        return (
            <div classname="chart-container">
                <AreaChart width={width} height={height} data={data} margin={{top: 10, right: 30, left: 0, bottom: 0}}>
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="dateStr"/>
                    <YAxis/>
                    <Tooltip/>
                    <Area type="monotone" dataKey="additions" stroke="none" fill="green" />
                    <Area type="monotone" dataKey="deletions" stroke="none" fill="red" />
                </AreaChart>
            </div>
        );
    }
}

export default CodeChangesChart;
