import React, { Component } from 'react';
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const students = [
    {name: 'Student A', progress: 50},
    {name: 'Student B', progress: 30},
    {name: 'Student C', progress: 17},
    {name: 'Student D', progress: 39},
    {name: 'Student E', progress: 0},
    {name: 'Student F', progress: 10},
    {name: 'Student G', progress: 0},
    {name: 'Student H', progress: 1},
    {name: 'Student I', progress: 15},
    {name: 'Student J', progress: 5},
    {name: 'Student K', progress: 20},
    {name: 'Student L', progress: 10},
    {name: 'Student M', progress: 100},
    {name: 'Student N', progress: 40},
    {name: 'Student O', progress: 0},
    {name: 'Student P', progress: 0},
];

const data = [];

const binCount = 6;
const binSize = 100 / binCount;

for (let i = 0; i < binCount; i++) {
    data[i] = {
        progressBin: `${parseInt(i * binSize)}-${parseInt((i + 1) * binSize)}%`,
        count: 0,
    }
}

for (let student of students) {
    const progressBin = Math.min(parseInt(student.progress / binSize), binCount-1);
    data[progressBin].count += 1;
}

class ClassProgressHistogram extends Component {
    render() {
        return (
            <ComposedChart layout="vertical" width={600} height={400} data={data}>
                <CartesianGrid/>
                <XAxis type="number"/>
                <YAxis dataKey="progressBin" type="category"/>
                <Tooltip/>
                <Legend/>
                <Bar dataKey="count" fill="#8884d8"/>
            </ComposedChart>
        );
    }
}

export default ClassProgressHistogram;
