import React, { Component } from 'react';
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Label } from 'recharts';

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

const defaultData = [];

const binCount = 5;
const binSize = 100 / binCount;

for (let i = 0; i < binCount; i++) {
    defaultData[i] = {
        progressBin: `${parseInt(i * binSize)}-${parseInt((i + 1) * binSize)}%`,
        count: 0,
    }
}

for (let student of students) {
    const progressBin = Math.min(parseInt(student.progress / binSize), binCount-1);
    defaultData[progressBin].count += 1;
}

for (let i = 0; i < binCount; i++) {
    defaultData[i].percent = defaultData[i].count / students.length;
}

const toPercent = (decimal, fixed = 0) => {
    return `${(decimal * 100).toFixed(fixed)}%`;
};

const AxisLabel = ({ axisType, x, y, width, height, stroke, children }) => {
    const isVert = axisType === 'yAxis';
    const cx = isVert ? x : x + (width / 2);
    const cy = isVert ? (height / 2) + y : y + height + 10;
    const rot = isVert ? `270 ${cx} ${cy}` : 0;
    return (
        <text x={cx} y={cy} transform={`rotate(${rot})`} textAnchor="middle" stroke={stroke}>
            {children}
        </text>
    );
};

class ClassProgressHistogram extends Component {
    render() {

        return (
            <div className="chart-container">
                <ComposedChart
                    width={this.props.width || 600}
                    height={this.props.height || 300}
                    data={this.props.data || defaultData}
                    margin={{top: 5, right: 30, left: 30, bottom: 35}}
                    barCategoryGap={0}
                >
                    <CartesianGrid/>
                    <XAxis dataKey="progressBin" type="category">
                        <Label offset={-10} position="insideBottom">
                            % Completion
                        </Label>
                    </XAxis>
                    <YAxis tickFormatter={toPercent}>
                        <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                            % of Class
                        </Label>
                    </YAxis>
                    <Tooltip/>
                    <Bar dataKey="percent" fill="#8884d8"/>
                </ComposedChart>
            </div>
        );
    }
}

export default ClassProgressHistogram;
