import React, { Component } from 'react'
import { ComposedChart, Bar, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Label, ResponsiveContainer } from 'recharts'
import { connect } from 'react-redux'
import { getTestBarGraph } from '../../redux/actions'
import url from '../../server'

const toPercent = (decimal, fixed = 0) => {
    return `${(decimal * 100).toFixed(fixed)}%`
}

const defaultData1 = [
    {
        'testName': 'Test 1',
        'percent': .98,
        'hidden': false,
    },
    {
        'testName': 'Test 2',
        'percent': .94,
        'hidden': false,
    },
    {
        'testName': 'Test 3',
        'percent': 1,
        'hidden': false,
    },
    {
        'testName': 'Test 4',
        'percent': .89,
        'hidden': true,
    },
]
const defaultData2 = [
    {
        'testName': 'Test 1',
        'percent': .07,
        'hidden': false,
    },
    {
        'testName': 'Test 2',
        'percent': .08,
        'hidden': false,
    },
    {
        'testName': 'Test 3',
        'percent': .01,
        'hidden': false,
    },
    {
        'testName': 'Test 4',
        'percent': .01,
        'hidden': false,
    },
    {
        'testName': 'Test 5',
        'percent': .01,
        'hidden': false,
    },
    {
        'testName': 'Test 6',
        'percent': .02,
        'hidden': true,
    },
]

class ClassTestCasePercentDone extends Component {
    constructor(props) {
        super(props)

        this.state = {
            formattedData: this.getDefaultData()
        }
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if (nextProps.data === null) {
            this.setState({ formattedData: this.getDefaultData(nextProps) })
        }
        if (this.props.isLoading && !nextProps.isLoading) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    getDefaultData = (props) => {
        return (props && props.projectID || this.props.projectID) == 'cs252 Fall2018: MyMalloc' ? defaultData1 : defaultData2
    }

    fetch = (props) => {
        if(props.projectID) {
            props.getData(`${url}/api/testSummary?projectID=${props.projectID}`)
        }   
    }

    formatApiData = (udata) => {
        if(!udata || !udata.data || udata.data.length === 0) {
            return this.getDefaultData()
        }

        const data = udata.data
        const formattedData = []

        for (let apiEntry of data) {
            const entry = {
                testName: apiEntry.testName,
                hidden: apiEntry.hidden,
                percent: apiEntry.score / 100,
                score: apiEntry.score,
            }

            formattedData.push(entry)
        }

        return formattedData
    }

    render() {
        return (
            !this.props.isLoading 
            ? <div className="chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <ComposedChart
                        data={this.state.formattedData}
                        margin={{top: 5, right: 30, left: 30, bottom: 35}}
                    >
                        <CartesianGrid/>
                        <XAxis dataKey="testName" type="category">
                            <Label offset={-10} position="insideBottom">
                                Test Case
                            </Label>
                        </XAxis>
                        <YAxis tickFormatter={toPercent} domain={[0, 1]}>
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                % of Class
                            </Label>
                        </YAxis>
                        <Tooltip/>
                        <Bar dataKey="percent">
                            {this.state.formattedData.map((entry, index) => (
                                <Cell key={Date.now()+index} fill={entry.hidden ? '#005599' : '#8884d8' }/>
                            ))}
                        </Bar>
                    </ComposedChart>
                </ResponsiveContainer>
            </div>
            : <div>{/* TODO: add spinner */}Loading</div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        data: state.course && state.course.getTestBarGraphData ? state.course.getTestBarGraphData : null,
        isLoading: state.course ? state.course.getTestBarGraphIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getData: (url, headers, body) => dispatch(getTestBarGraph(url, headers, body))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ClassTestCasePercentDone)
