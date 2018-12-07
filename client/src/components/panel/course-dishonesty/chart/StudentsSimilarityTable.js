import React, { Component } from 'react' 
import { ScatterChart, Scatter, XAxis, YAxis, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import {LoadingIcon} from '../../../Helpers'
import {
    retrieveStudentsSimilarity
} from '../../../../redux/retrievals/course'
import {getStudentsSimilarity} from '../../../../redux/state-peekers/course'
import {getCurrentProject} from '../../../../redux/state-peekers/projects'

class StudentsSimilarityTable extends Component {
    
    componentDidMount() {
        // Done in StudentsSimilarity
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
        
        const average = (chart.data.reduce((sum, item) => sum + item.similarity, 0) / chart.data.length).toFixed(0)
        return (
            <div
                title='Histogram of all students in the course grouped by the percentage of tests they are passing (progress).'
                style={{'padding': '10px'}}
            >
                <table>
                    <tbody>
                        {chart.data.reverse().slice(0, 10).map((item, index)=>{
                            return(
                                <tr key={item.user1 + ' ' + item.user2}>
                                    <td style={{'padding': '3px'}}>{item.user1}</td>
                                    <td style={{'padding': '3px'}}>{item.user2}</td>
                                    <td style={{'padding': '3px'}}>{item.similarity}</td>
                                </tr>
                            )
                        })}
                        <tr>
                            <td style={{'padding': '3px'}}>Average</td>
                            <td style={{'padding': '3px'}}></td>
                            <td style={{'padding': '3px'}}>{average}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
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
