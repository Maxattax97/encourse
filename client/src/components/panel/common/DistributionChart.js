import {Component} from 'react'
import {Bar, ComposedChart, Label, ReferenceLine, ResponsiveContainer, Tooltip, XAxis, YAxis} from 'recharts'

import connect from 'react-redux/es/connect/connect'
import React from 'react'
import {Chart} from '../../Helpers'
import {getSelected} from '../../../redux/state-peekers/control'
import {getCourseCharts, getCourseFilterCharts} from '../../../redux/state-peekers/course'
import moment from 'moment'

class DistributionChart extends Component {

    renderChart() {
        if(!this.props.chart)
            return null;

        const chart = JSON.parse(JSON.stringify(this.props.chart));

        if(!chart.bars || !chart.bars.length || (Math.abs(chart.courseStats.min - chart.courseStats.max) <= .005))
            return null;

        if(!this.props.filterCharts.loading && !this.props.filterCharts.error)
            this.props.filterChart.bars.forEach(bar => {
                chart.bars.forEach(bar1 => {
                    if(Math.abs(bar.index - bar1.index) <= .05) {
                        bar1["size1"] = bar.size
                        bar1["size"] -= bar.size
                    }
                })
            })

        return (
            <ResponsiveContainer
                width="100%"
                height="100%"
            >
                <ComposedChart
                    data={chart.bars}
                    margin={{top: 5, right: 10, left: 5, bottom: 15}}
                    barCategoryGap={0}
                >
                    <XAxis type="number" dataKey="index">
                        <Label offset={-10} position="insideBottom">
                            { this.props.x }
                        </Label>
                    </XAxis>
                    <YAxis type="number" domain={[0, 'dataMax']}>
                        <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                            { this.props.y }
                        </Label>
                    </YAxis>
                    <Tooltip animationDuration={100} labelFormatter={tick => tick.toFixed(2) + " " + this.props.x}/>
                    {
                        !this.props.filterCharts.loading && !this.props.filterCharts.error ?
                            <Bar dataKey="size1" fill="#0057A7CC" stackId="a" isAnimationActive={false}/>
                            : null
                    }
                    {
                        !this.props.filterCharts.loading && !this.props.filterCharts.error ?
                            <Bar dataKey="size" fill="gray" stackId="a" isAnimationActive={false}/>
                            :
                            <Bar dataKey="size" fill="#0057A7CC" isAnimationActive={false}/>
                    }
                </ComposedChart>
            </ResponsiveContainer>
        )
    }

    render() {

        return (
            <Chart
                chart={this.props.charts}
            >
                {
                    this.renderChart()
                }
            </Chart>
        )
    }
}

const mapStateToProps = (state, props) => {
    return {
        selected: getSelected(state, 'students'),
        charts: getCourseCharts(state),
        filterCharts: getCourseFilterCharts(state),
    }
}

export default connect(mapStateToProps)(DistributionChart)