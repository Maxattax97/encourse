import React, { Component } from 'react' 
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'

import { getClassProgress } from '../../../../redux/actions/index'
import url from '../../../../server'
import {LoadingIcon} from '../../../Helpers'
// import CustomTooltipContent from './CustomTooltipContent';

const toPercent = (decimal, fixed = 2) => {
    return `${(decimal).toFixed(fixed)}`
}

const defaultData = [
    {
        'user1': 'user1',
        'user2': 'user2',
        'user1index': 1,
        'user2index': 2,
        'simularity': 10,
    },
    {
        'user1': 'user1',
        'user2': 'user3',
        'user1index': 1,
        'user2index': 3,
        'simularity': 11,
    },
    {
        'user1': 'user1',
        'user2': 'user4',
        'user1index': 1,
        'user2index': 4,
        'simularity': 9,
    },
    {
        'user1': 'user2',
        'user2': 'user3',
        'user1index': 2,
        'user2index': 3,
        'simularity': 30,
    },
    {
        'user1': 'user2',
        'user2': 'user4',
        'user1index': 2,
        'user2index': 4,
        'simularity': 13,
    },
    {
        'user1': 'user3',
        'user2': 'user4',
        'user1index': 3,
        'user2index': 4,
        'simularity': 12,
    },
];

class StudentIdenticalLines extends Component {
    constructor(props) {
        super(props)

        this.state = {
            formattedData: defaultData,
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }
    
    componentWillReceiveProps = (nextProps) => {
        if(this.props.isLoading && !nextProps.isLoading) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.currentProjectId !== this.props.currentProjectId) {
            this.fetch(nextProps)
        }
    }
    
    fetch = (props) => {
        if(props.currentProjectId) {
            // TODO find endpoint
            // props.getData(`${url}/api/classProgress?projectID=${props.currentProjectId}`)
        }    
    }

    formatApiData = (udata) => {
        // TODO fix
        return defaultData
    }

    render() {
        return (
            this.props.isLoading !== undefined && !this.props.isLoading
                ? <div className="chart-container">
                    <p>Chart not finished</p>
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
        // TODO map data to correct redux state
        // data: state.course && state.course.getClassProgressData ? state.course.getClassProgressData : null,
        isLoading: state.course ? state.course.getClassProgressIsLoading : false,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        // TODO map to correct dispatch
        getData: (url, headers, body) => dispatch(getClassProgress(url, headers, body))
    }
}

export { StudentIdenticalLines }
export default connect(mapStateToProps, mapDispatchToProps)(StudentIdenticalLines)
