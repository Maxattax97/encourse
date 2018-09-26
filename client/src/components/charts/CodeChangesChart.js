import React, { Component } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Label, Legend, ResponsiveContainer } from 'recharts';


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
        return (
            <div className="chart-container" style={{"height": "500px"}}>
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={this.props.data || defaultData} margin={{top: 40, right: 30, left: 0, bottom: 25}}>
                        <text x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Code Frequency</text>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <XAxis dataKey="dateStr">
                            <Label position="insideBottom" offset={-15} value="Date"/>
                        </XAxis>
                        <YAxis/>
                        <Tooltip/>
                        <Area type="monotone" dataKey="additions" stroke="none" fill="green" />
                        <Area type="monotone" dataKey="deletions" stroke="none" fill="red" />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

export default CodeChangesChart;
