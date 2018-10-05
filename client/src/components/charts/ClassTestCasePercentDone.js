import React, { Component } from 'react';
import { ComposedChart, Bar, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Label, ResponsiveContainer } from 'recharts';
import { connect } from 'react-redux'

// TODO change api
// import { getXXXXX } from '../../redux/actions'
import url from '../../server'

const toPercent = (decimal, fixed = 0) => {
    return `${(decimal * 100).toFixed(fixed)}%`;
};

const defaultData1 = [
    {
        "testName": "Test 1",
        "percent": .98,
        "hidden": false,
    },
    {
        "testName": "Test 2",
        "percent": .94,
        "hidden": false,
    },
    {
        "testName": "Test 3",
        "percent": 1,
        "hidden": false,
    },
    {
        "testName": "Test 4",
        "percent": .89,
        "hidden": true,
    },
]
const defaultData2 = [
    {
        "testName": "Test 1",
        "percent": .07,
        "hidden": false,
    },
    {
        "testName": "Test 2",
        "percent": .08,
        "hidden": false,
    },
    {
        "testName": "Test 3",
        "percent": .01,
        "hidden": false,
    },
    {
        "testName": "Test 4",
        "percent": .01,
        "hidden": false,
    },
    {
        "testName": "Test 5",
        "percent": .01,
        "hidden": false,
    },
    {
        "testName": "Test 6",
        "percent": .02,
        "hidden": true,
    },
]

class ClassTestCasePercentDone extends Component {
    constructor(props) {
        super(props);

        this.state = {
            formattedData: this.getDefaultData()
        };
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if (nextProps.data === null) {
            this.setState({ formattedData: this.getDefaultData(nextProps) })
        }
        if (!this.props.isFinished && nextProps.isFinished) {
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
        props.getData(`${url}/api/testSummary?projectID=${props.projectID}`,
        {'Authorization': `Bearer ${props.token}`})
    }

    formatApiData = (udata) => {
        const data = udata.data;

        if (!data || data.length === 0) {
            return this.getDefaultData();
        }

        const formattedData = []

        for (let apiEntry of data) {
            const entry = {
                testName: apiEntry.testName,
                hidden: apiEntry.hidden,
                percent: apiEntry.score / 100,
                score: apiEntry.score,
            }

            formattedData.push(entry);
        }

        return formattedData;
    }

    render() {
        return (
            <div className="chart-container">
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
                        <YAxis tickFormatter={toPercent}>
                            <Label angle={-90} position='insideLeft' style={{ textAnchor: 'middle' }}>
                                % of Class
                            </Label>
                        </YAxis>
                        <Tooltip/>
                        <Bar dataKey="percent">
                            {this.state.formattedData.map((entry, index) => (
                                <Cell fill={entry.hidden ? '#005599' : '#8884d8' }/>
                            ))}
                        </Bar>
                    </ComposedChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        // TODO change api
        data: state.course && state.course.getXXXXX ? state.course.getXXXXX : null,
        isLoading: state.course ? state.course.getXXXXX : false,
        isFinished: state.course ? state.course.getXXXXX : false,
    }
  }

const mapDispatchToProps = (dispatch) => {
    return {
        // TODO change api
        // getData: (url, headers, body) => dispatch(getXXXXX(url, headers, body))
        getData: () => {}
    }
}

export { ClassTestCasePercentDone }
export default connect(mapStateToProps, mapDispatchToProps)(ClassTestCasePercentDone)
