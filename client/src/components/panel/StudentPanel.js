import React, { Component } from 'react'
import { connect } from 'react-redux'

import {BackNav, Card, Summary} from '../Helpers'
import StudentProgressLineGraph from '../chart/StudentProgressLineGraph'
import CodeChangesChart from '../chart/CodeChangesChart'
import CommitFrequencyHistogram from '../chart/CommitFrequencyHistogram'
import ProgressPerTime from '../chart/ProgressPerTime'
import ProgressPerCommit from '../chart/ProgressPerCommit'
import ProjectNavigation from '../navigation/ProjectNavigation'
import StudentStatistics from './util/StudentStatistics'
import CommitHistory from './util/CommitHistory'
import { history } from '../../redux/store'
import { getStudent, clearStudent } from '../../redux/actions/index'
import ActionNavigation from '../navigation/ActionNavigation'
import StudentFeedback from './util/StudentFeedback'


class StudentPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            modal_blur: ''
        }
    }

    componentWillMount = () => this.clear()
    componentWillUnmount = () => this.clear()
    clear = () => this.props.clearStudent()

    componentDidMount = () => {
        if(!this.props.currentStudent) {
            this.props.getStudent(/*TODO: add individual student call for case that currentStudent isn't stored*/)
        }
    }

    back = () => {
        history.push('/course')
    };

    render() {

        const action_names = [
            'View Hidden Tests',
            'Run Tests',
            'Academic Dishonesty Report'
        ]

        const actions = [
            () => {},
            () => {},
            () => {}
        ]

        return (
            <div className="panel-student">

                <div className='panel-left-nav'>
                    <BackNav back="Course"
                        backClick={ this.back }/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                    <ProjectNavigation/>
                </div>

                <div className="panel-right-nav">
                    <div className='top-nav' />
                    <CommitHistory projectID={this.props.currentProjectId} id={this.props.currentStudent.id} />
                </div>

                <div className="panel-center-content">
                    <div className={ `panel-student-content${this.state.modal_blur}` }>
                        <h1 className='header'>
                            {
                                `CS252 - ${this.props.currentStudent ? `${this.props.currentStudent.first_name} ${this.props.currentStudent.last_name}` : ''}`
                            }
                        </h1>
                        <div className="h1 break-line header" />

                        <Summary
                            columns={ 2 }
                            data={ [
                                <StudentProgressLineGraph projectID={this.props.currentProjectId} id={this.props.currentStudent.id} key={1}/>,
                                <CodeChangesChart projectID={this.props.currentProjectId} id={this.props.currentStudent.id} key={2}/>,
                                <CommitFrequencyHistogram projectID={this.props.currentProjectId} id={this.props.currentStudent.id} key={3}/>,
                                <ProgressPerTime projectID={this.props.currentProjectId} id={this.props.currentStudent.id} key={4}/>,
                                <ProgressPerCommit projectID={this.props.currentProjectId} id={this.props.currentStudent.id} key={5}/>,
                            ] }
                            className='charts'
                            iterator={ (chart) =>
                                <Card key={ chart.key } >
                                    { chart }
                                </Card>
                            }>
                            <h3 className='header'>Student Charts</h3>
                        </Summary>

                        <div className="h1 break-line" />
                        <div className="student-stats-comments float-height">
                            <Card>
                                <StudentStatistics projectID={this.props.currentProjectId} id={this.props.currentStudent.id}/>
                            </Card>
                            <Card>
                                <StudentFeedback/>
                            </Card>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : 0
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentPanel)
