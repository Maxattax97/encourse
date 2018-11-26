import React, { Component } from 'react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, Label, Brush, ResponsiveContainer } from 'recharts'
import moment from 'moment'
import { connect } from 'react-redux'

import {Chart} from '../../../Helpers'
import {retrieveStudentCodeChanges} from "../../../../redux/retrievals/student"
import {
	getCurrentStudent,
	getStudentCodeChanges
} from "../../../../redux/state-peekers/student"
import {getCurrentProject} from "../../../../redux/state-peekers/project"

class CodeChanges extends Component {

	componentWillMount() {
		if(this.props.student && this.props.project)
			retrieveStudentCodeChanges(this.props.student, this.props.project)
	}

	componentWillReceiveProps(nextProps) {
		if(nextProps.student && nextProps.project && (!(this.props.project) || !(this.props.student) || this.props.project.index !== nextProps.project.index))
			retrieveStudentCodeChanges(nextProps.student, nextProps.project)
	}

    dateFormatter = (date) => {
        return moment(date).format('M-D')
    }

    render() {
        return (
            <Chart chart={this.props.chart}>
                    <ResponsiveContainer width="100%" height="100%">
                        <AreaChart data={this.props.chart.data} margin={{top: 40, right: 35, left: 0, bottom: 25}}>
                            <text className="chart-title" x="50%" y="15px" textAnchor="middle" dominantBaseline="middle">Lines of Code Added/Deleted</text>
                            <CartesianGrid strokeDasharray="3 3"/>
                            <XAxis type="number" dataKey="date" domain={['dataMin', 'dataMax']} tickFormatter={this.dateFormatter}>
                                <Label position="insideBottom" offset={-15} value="Date"/>
                            </XAxis>
                            <YAxis/>
                            <Tooltip labelFormatter={this.dateFormatter}/>
                            <Area name="deletions" type="monotone" dataKey="deletions" stroke="none" fill="red" />
                            <Area name="additions" type="monotone" dataKey="additions" stroke="none" fill="green" />
                            <Brush dataKey="date" height={20} stroke="#8884d8" tickFormatter={this.dateFormatter}/>
                        </AreaChart>
                    </ResponsiveContainer>
            </Chart>
        )
    }
}

const mapStateToProps = (state) => {
	return {
		student: getCurrentStudent(state),
		project: getCurrentProject(state),
		chart: getStudentCodeChanges(state)
	}
}

export default connect(mapStateToProps)(CodeChanges)
