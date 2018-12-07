import React from 'react'
import { Brush, LineChart, Line, ResponsiveContainer } from 'recharts'
import {getStudentProgress} from '../../../../redux/state-peekers/student'
import { connect } from 'react-redux'
import {Chart} from '../../../Helpers'
import moment from 'moment'

const dateFormatter = (dateUnix) => {
    return moment(dateUnix).format('M-D')
}

const BrushSlider = (props) => {
    const {chart} = props
    return (
        <div style={{'height': '90px'}}>
            <Chart chart={chart}>
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={chart.data} syncId="date" margin={{top: 0, right: 60, left: 60, bottom: 0}}>
                        <Line style={{'display': 'none'}} dataKey="date" />
                        <Brush dataKey="date" height={60} stroke="#008000" tickFormatter={dateFormatter} x={60} y={20}/>
                    </LineChart>
                </ResponsiveContainer>
            </Chart>
        </div>
    )
}

const mapStateToProps = (state) => {
    return {
        chart: getStudentProgress(state)
    }
}

export default connect(mapStateToProps)(BrushSlider)
