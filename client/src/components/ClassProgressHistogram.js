import React, { Component } from 'react';
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const data = [
    {name: 'Student A', progress: .50},
    {name: 'Student B', progress: .30},
    {name: 'Student C', progress: .50},
    {name: 'Student D', progress: .50},
    {name: 'Student E', progress: .00},
    {name: 'Student F', progress: .60},
    {name: 'Student G', progress: .50},
];

class ClassProgressHistogram extends Component {
    render() {
        return (
            <ComposedChart layout="vertical" width={600} height={400} data={data}>
                <CartesianGrid/>
                <XAxis type="number"/>
                <YAxis dataKey="name" type="category"/>
                <Tooltip/>
                <Legend/>
                <Bar dataKey="progress" fill="#8884d8"/>
            </ComposedChart>
        );
    }
}

export default ClassProgressHistogram;
