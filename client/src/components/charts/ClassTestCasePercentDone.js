import React, { Component } from 'react';
import { ComposedChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Label, ResponsiveContainer } from 'recharts';
import { connect } from 'react-redux'

// TODO change api
import { getClassProgress } from '../../redux/actions'
import url from '../../server'

const toPercent = (decimal, fixed = 0) => {
    return `${(decimal * 100).toFixed(fixed)}%`;
};

const defaultData = [
    {
        "testName": "Test 1",
        "percent": .50,
    },
    {
        "testName": "Test 2",
        "percent": .23,
    },
    {
        "testName": "Test 3",
        "percent": .94,
    },
    {
        "testName": "Test 4",
        "percent": .38,
    },
]

class ClassTestCasePercentDone extends Component {
    constructor(props) {
        super(props);

        this.state = {
            formattedData: defaultData,
        };
    }

    componentDidMount = () => {
        this.fetch(this.props)
    }

    componentWillReceiveProps = (nextProps) => {
        if(!this.props.isFinished && nextProps.isFinished) {
            this.setState({ formattedData: this.formatApiData(nextProps.data) })
        }
        if (nextProps.projectID !== this.props.projectID) {
            this.fetch(nextProps)
        }
    }

    fetch = (props) => {
        // TODO change api
        props.getData(`${url}/secured/classProgress?projectID=${props.projectID}`,
        {'Authorization': `Bearer ${props.token}`})
    }

    formatApiData = (udata) => {
        const data = udata.data;

        if (!data || data.length === 0) {
            return defaultData;
        }

        const formattedData = []

        for (let apiEntry of data) {
            const entry = {
                testName: apiEntry.testName,
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
                        barCategoryGap={0}
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
                        <Bar dataKey="percent" fill="#8884d8"/>
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
        data: state.course && state.course.getClassProgressData ? state.course.getClassProgressData : null,
        isLoading: state.course ? state.course.getClassProgressData : false,
    }
  }

const mapDispatchToProps = (dispatch) => {
    return {
        // TODO change api
        getData: (url, headers, body) => dispatch(getClassProgress(url, headers, body))
    }
}

export { ClassTestCasePercentDone }
export default connect(mapStateToProps, mapDispatchToProps)(ClassTestCasePercentDone)
