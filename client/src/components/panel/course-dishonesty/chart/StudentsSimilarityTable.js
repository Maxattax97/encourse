import React, { Component } from 'react' 
import { ScatterChart, Scatter, XAxis, YAxis, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import {LoadingIcon} from '../../../Helpers'
import {
    retrieveStudentsSimilarity
} from '../../../../redux/retrievals/course'
import {getStudentsSimilarity} from '../../../../redux/state-peekers/course'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'

import Statistics from "../../common/Statistics"

class StudentsSimilarityTable extends Component {
    
    componentDidMount() {
        // if (this.props.project)
        //     retrieveStudentsSimilarity(this.props.project)
    }
    
    componentDidUpdate = (prevProps) => {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveStudentsSimilarity(this.props.project)
    }
    
    render() {
        const {chart} = this.props
        
        if (chart.loading) {
            return (
            <div className='chart-container loading' title={ this.props.title ? this.props.title : null}>
            <LoadingIcon/>
            </div>
            )
        }
        
        // TODO move this to redux
        // const average = (chart.data.reduce((sum, item) => sum + item.similarity, 0) / chart.data.length).toFixed(0)
        chart.data.sort((a, b) => b.similarity - a.similarity)
        const data = chart.data.slice(0, 10)
        const stats = {
            data: data.map((item) => {
                item.stat_name = `${item.user1} ${item.user2}`
                item.stat_value = item.similarity
                return item
            }),
            loading: false,
        }
        // title='Histogram of all students in the course grouped by the percentage of tests they are passing (progress).'
        return (
            <Statistics stats={stats} noColumns>
                <h4 className='header'>
                    Students with Identical Lines
                </h4>
                <div className='h5 break-line' />
            </Statistics>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        chart: getStudentsSimilarity(state),
        project: getCurrentProject(state)
    }
}

export default connect(mapStateToProps)(StudentsSimilarityTable)
