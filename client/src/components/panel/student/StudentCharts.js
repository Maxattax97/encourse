import React, { Component } from 'react'
import CommitFrequencyHistogram from './chart/CommitFrequency'
import {ChartList} from '../../Helpers'
import {getStudents} from '../../../redux/state-peekers/course'
import {getCurrentProject} from '../../../redux/state-peekers/projects'
import connect from 'react-redux/es/connect/connect'
import {getCurrentStudent} from '../../../redux/state-peekers/student'
import {retrieveStudentCharts} from '../../../redux/retrievals/student'
import CodeChanges from './chart/CodeChanges'
import StudentProgress from './chart/StudentProgress'
import MinutesWorkedLine from './chart/MinutesWorkedLine'

class StudentCharts extends Component {

    componentDidMount() {
        if (this.props.project && this.props.student)
            retrieveStudentCharts(this.props.project, this.props.student)
    }

    componentDidUpdate(prevProps) {
        if (this.props.project !== prevProps.project || this.props.project !== prevProps.student)
            retrieveStudentCharts(this.props.project, this.props.student)
    }

    render() {
        if(this.props.project && this.props.project.runTestall)
            return (
                <ChartList>
                    <CommitFrequencyHistogram />
                    <CodeChanges />
                    <MinutesWorkedLine/>
                    <StudentProgress/>
                </ChartList>
            )

        return (
            <ChartList>
                <CommitFrequencyHistogram />
                <CodeChanges />
                <MinutesWorkedLine/>
            </ChartList>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        project: getCurrentProject(state),
        student: getCurrentStudent(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentCharts)
