import React, { Component } from 'react';
import { LineChart, CartesianGrid, XAxis, YAxis, 
    Tooltip, Legend, Line, Label } from 'recharts'
import '../../css/StudentProgressLineGraph.css'

class StudentProgressLineGraph extends Component {
    render() {
        const data = [
            {name: '9/10', progress: 0},
            {name: '9/11', progress: 20},
            {name: '9/12', progress: 20},
            {name: '9/13', progress: 30},
            {name: '9/14', progress: 50},
            {name: '9/15', progress: 60},
            {name: '9/16', progress: 100},
          ]

        return (
          <div className="chart-container">
            <h3 className="chart-title">Student Progress Over Time</h3>
            <LineChart className="chart" width={730} height={500} data={data}
             margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name">
                    <Label value="Date" position="bottom" />
                </XAxis>
                <YAxis>
                    <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                        % Completion
                    </Label>
                </YAxis>
                <Tooltip />
                <Legend verticalAlign="top"/>
                <Line type="monotone" dataKey="progress" stroke="#8884d8" />
            </LineChart>
          </div>
        )
    }
}

export default StudentProgressLineGraph;