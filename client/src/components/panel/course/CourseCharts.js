import React, {Component} from 'react'
import CompletionProgress from './chart/CompletionProgress'
import TestCaseProgress from './chart/TestCaseProgress'
import {ChartList} from "../../Helpers"
import {
    getCourseCharts,
    getCourseFilterCharts,
    getStudents
} from '../../../redux/state-peekers/course'
import {getCurrentProject} from '../../../redux/state-peekers/projects'
import {getFilters, getFiltersHaveChanged} from '../../../redux/state-peekers/control'
import connect from 'react-redux/es/connect/connect'
import { retrieveCourseCharts, retrieveCourseFilterCharts} from '../../../redux/retrievals/course'
import DistributionChart from '../common/DistributionChart'

class CourseCharts extends Component {

    componentDidMount() {
        if(this.props.project) {
            retrieveCourseCharts(this.props.project)

            retrieveCourseFilterCharts(this.props.project, null, null, null, null, null, true)
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.project !== prevProps.project) {
            retrieveCourseCharts(this.props.project)

            retrieveCourseFilterCharts(this.props.project, null, null, null, null, null, true)
        }
    }

    render() {

        if(this.props.project && this.props.project.runTestall)
            return (
                <ChartList>
                    <CompletionProgress/>
                    <TestCaseProgress/>
                </ChartList>
            )

        return (
            <ChartList>
                <DistributionChart chart={ (this.props.charts.data || {}).commits } filterChart={ (this.props.filterCharts.data || {}).commits } x="Commits" y="Student Count"/>
                <DistributionChart chart={ (this.props.charts.data || {}).time } filterChart={ (this.props.filterCharts.data || {}).time } x="Minutes" y="Student Count"/>
            </ChartList>
        )
	}
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        project: getCurrentProject(state),
        filters: getFilters(state),
        charts: getCourseCharts(state),
        filterCharts: getCourseFilterCharts(state),
        filtersHaveChanged: getFiltersHaveChanged(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CourseCharts)