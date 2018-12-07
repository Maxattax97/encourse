import React, { Component } from 'react'
import { LineChart, CartesianGrid, XAxis, YAxis,
    Tooltip, Legend, Line, Label, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'
import {getCurrentStudent} from '../../../../redux/state-peekers/student'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'
import {Chart} from '../../../Helpers'
import {getDishonestyReport} from '../../../../redux/state-peekers/course'
import {retrieveDishonestyReport} from '../../../../redux/retrievals/course'
import RadarChart from 'recharts/es6/chart/RadarChart'
import PolarAngleAxis from 'recharts/es6/polar/PolarAngleAxis'
import PolarRadiusAxis from 'recharts/es6/polar/PolarRadiusAxis'
import Radar from 'recharts/es6/polar/Radar'
import PolarGrid from 'recharts/es6/polar/PolarGrid'

class PercentileRadar extends Component {

    constructor(props) {
        super(props)

        this.state = {
            data: [
                {measure: 'rate'},
                {measure: 'velocity'},
                {measure: 'score'}
            ]
        }
    }

    componentDidMount() {
        if(this.props.project)
            retrieveDishonestyReport(this.props.project)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveDishonestyReport(this.props.project)
    }

    dateFormatter = (dateUnix) => {
        return moment(dateUnix).format('M-D')
    }

    render() {
        if(!this.props.project || !this.props.student)
            return null

        const student = this.props.chart.data.filter(student => student.id === this.props.student.id)[0]

        if(!student)
            return null

        const data = [
            {measure: 'rate', score: student.metrics.rate.percentile, maxScore: 1},
            {measure: 'velocity', score: student.metrics.velocity.percentile, maxScore: 1},
            {measure: 'score', score: student.score, maxScore: 1}
        ]

        console.log(student)

        return (
            <Chart chart={this.props.chart}>
                <ResponsiveContainer width="100%" height="100%">
                    <RadarChart className="chart" width={730} height={500} cx={365} cy={250} outerRadius={150} data={data} margin={{ top: 20, right: 35, left: 20, bottom: 20 }}>
                        <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Student Dishonesty Scores</text>
                        <PolarGrid />
                        <PolarAngleAxis dataKey="measure" />
                        <PolarRadiusAxis/>
                        <Tooltip/>
                        <Radar dataKey="score" stroke="#0057A7CC" fill="#0057A7"/>
                    </RadarChart>
                </ResponsiveContainer>
            </Chart>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        student: getCurrentStudent(state),
        project: getCurrentProject(state),
        chart: getDishonestyReport(state),
    }
}

export default connect(mapStateToProps)(PercentileRadar)
