import React, { Component } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Label, Legend, ResponsiveContainer } from 'recharts';
import moment from 'moment'

function formatApiData(data) {
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

function dateFormatter(dateUnix) {
    const m = moment(dateUnix);
    return m.format('MM-DD')
}

class CodeChangesChart extends Component {
    constructor(props) {
        super(props);

        let data = props.data;
        data = formatApiData(data);

        this.state = {
            data,
        };
    }

    render() {
        return (
            <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={this.state.data} margin={{top: 40, right: 30, left: 0, bottom: 25}}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Code Frequency</text>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <XAxis type="number" dataKey="date" domain={['dataMin', 'dataMax']} tickFormatter={dateFormatter}>
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
