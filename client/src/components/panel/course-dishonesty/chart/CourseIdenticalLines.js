import React, { Component } from 'react' 
import { ScatterChart, Scatter, XAxis, YAxis, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'
import CustomTooltipContent from './CustomTooltipContent';

import { getSimilarityPlot } from '../../../../redux/actions'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'

const defaultData = [
    {
        'user1': 'user1',
        'user2': 'user2',
        'user1index': 1,
        'user2index': 2,
        'similarity': 10,
        'similarity_bin': 10,
        'height': 2
    },
    {
        'user1': 'user1',
        'user2': 'user3',
        'user1index': 1,
        'user2index': 3,
        'similarity_bin': 10,
        'similarity': 11,
        'height': 3
    },
    {
        'user1': 'user1',
        'user2': 'user4',
        'user1index': 1,
        'user2index': 4,
        'similarity_bin': 10,
        'similarity': 9,
        'height': 1
    },
    {
        'user1': 'user2',
        'user2': 'user3',
        'user1index': 2,
        'user2index': 3,
        'similarity_bin': 30,
        'similarity': 30,
        'height': 1
    },
    {
        'user1': 'user2',
        'user2': 'user4',
        'user1index': 2,
        'user2index': 4,
        'similarity_bin': 10,
        'similarity': 13,
        'height': 5
    },
    {
        'user1': 'user3',
        'user2': 'user4',
        'user1index': 3,
        'user2index': 4,
        'similarity_bin': 10,
        'similarity': 12,
        'height': 4
    },
];

class CourseIdenticalLinesChart extends Component {
    constructor(props) {
        super(props)

        this.state = {
            formattedData: defaultData,
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }
    
    componentDidUpdate = (prevProps) => {
        if(prevProps.isLoading && !this.props.isLoading) { 
            this.setState({ formattedData: this.formatApiData(this.props.data) })
        }
        if (prevProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(this.props)
        }
    }
    
    fetch = (props) => {
        if(props.currentProjectId) {
            props.getData(`${url}/api/classSimilar?projectID=${props.currentProjectId}`)
        }    
    }

    formatApiData = (udata) => {
        if(!udata || udata.data)
            return []
        let data = udata.data
        console.log(data[0]);
        for (let item of data) {
            item.similarity_bin *= 10
        }
        data.sort((a, b) => {
            if (a.similarity_bin === b.similarity_bin) {
                return a.height - b.height
            }
            return a.similarity_bin - b.similarity_bin
        })
        return data
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <ResponsiveContainer width="100%" height="100%">
                        <ScatterChart
                            data={this.state.formattedData}
                            margin={{top: 5, right: 30, left: 30, bottom: 35}}
                            barCategoryGap={0}
                        >
                            <XAxis dataKey="similarity_bin" type="number">
                                <Label offset={-10} position="insideBottom">
                                    Identical lines of code
                                </Label>
                            </XAxis>
                            <YAxis dataKey="height" type="number">
                                <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                    Count
                                </Label>
                            </YAxis>
                            <Tooltip content={<CustomTooltipContent />} />
                            <Scatter dataKey="height" fill="#8884d8"/>
                        </ScatterChart>
                    </ResponsiveContainer>
                </div>
                :
                <div className='chart-container loading'>
                    <LoadingIcon/>
                </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        data: state.course && state.course.getSimilarityPlotData ? state.course.getSimilarityPlotData : null,
        isLoading: state.course ? state.course.getSimilarityPlotIsLoading : false,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getSimilarityPlot(url, headers, body))
    }
}

export { CourseIdenticalLinesChart }
export default connect(mapStateToProps, mapDispatchToProps)(CourseIdenticalLinesChart)
