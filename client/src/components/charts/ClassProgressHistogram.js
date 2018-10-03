import React, { Component } from 'react';
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Label, ResponsiveContainer } from 'recharts';
import { connect } from 'react-redux'

import { getClassProgress } from '../../redux/actions'

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
    constructor(props) {
        super(props);

        const defaultData = [
            {
                "progressBin": "0-20%",
                "order": 0,
                "count": 10,
                "percent": 0.625
            },
            {
                "progressBin": "20-40%",
                "order": 20,
                "count": 3,
                "percent": 0.1875
            },
            {
                "progressBin": "40-60%",
                "order": 40,
                "count": 2,
                "percent": 0.125
            },
            {
                "progressBin": "60-80%",
                "order": 60,
                "count": 0,
                "percent": 0
            },
            {
                "progressBin": "80-100%",
                "order": 80,
                "count": 1,
                "percent": 0.0625
            }
        ]

        this.state = {
            formattedData: defaultData,
        };
    }

    componentDidMount = () => {
        this.props.getData(/*TODO: add endpoint*/)
    }

    formatApiData = (data) => {
        const formattedData = []
        const data2 = Object.entries(data);
        const total = data2.reduce((sum, p) => sum + p[1], 0);

        for (let apiEntry of data2) {
            const entry = {
                progressBin: apiEntry[0],
                order: parseInt(apiEntry[0]),
                count: apiEntry[1],
                percent: apiEntry[1] / total,
            }

            formattedData.push(entry);
        }

        formattedData.sort((a, b) => a.order - b.order)

        return formattedData;
    }

    render() {
        return (
            <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <ComposedChart
                        data={this.state.formattedData}
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
                </ResponsiveContainer>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return { 
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        data: state.course && state.course.getClassProgressData ? state.course.getClassProgressData : null,
        isLoading: state.course ? state.course.getClassProgressData : false,
    }
  }

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getClassProgress(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ClassProgressHistogram)
