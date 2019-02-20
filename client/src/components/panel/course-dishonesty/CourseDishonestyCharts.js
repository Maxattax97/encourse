import React, { Component } from 'react'
import {Card, ChartList, Summary} from '../../Helpers'
import {
    retrieveCourseDishonestyCharts, retrieveCourseDishonestyFilterCharts,
} from '../../../redux/retrievals/course'
import connect from 'react-redux/es/connect/connect'
import {getCourseCharts, getCourseFilterCharts, getStudents} from '../../../redux/state-peekers/course'
import {getCurrentProject} from '../../../redux/state-peekers/projects'
import {getFilters, getFiltersHaveChanged} from '../../../redux/state-peekers/control'
import DistributionChart from '../common/DistributionChart'

class CourseDishonestyCharts extends Component {

    componentDidMount() {
        if(this.props.project) {
            retrieveCourseDishonestyCharts(this.props.project)

            retrieveCourseDishonestyFilterCharts(this.props.project, null, null, null, null, null, true)
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.project !== prevProps.project) {
            retrieveCourseDishonestyCharts(this.props.project)

            retrieveCourseDishonestyFilterCharts(this.props.project, null, null, null, null, null, true)
        }
    }

	render() {
		return (
			<ChartList>
                <DistributionChart chart={ (this.props.charts.data || {}).changes } filterChart={ (this.props.filterCharts.data || {}).changes } x="Additions per Deletion" y="Student Count"/>
                <DistributionChart chart={ (this.props.charts.data || {}).timeVelocity } filterChart={ (this.props.filterCharts.data || {}).timeVelocity } x="Progress per Minute" y="Student Count"/>
                <DistributionChart chart={ (this.props.charts.data || {}).commitVelocity } filterChart={ (this.props.filterCharts.data || {}).commitVelocity } x="Progress per Commit" y="Student Count"/>
                <DistributionChart chart={ (this.props.charts.data || {}).similarity } filterChart={ (this.props.filterCharts.data || {}).similarity } x="Lines of Code Similar" y="Student Comparison Count"/>
                <DistributionChart chart={ (this.props.charts.data || {}).similarityPercent } filterChart={ (this.props.filterCharts.data || {}).similarityPercent } x="Percent of Code Similar" y="Student Count"/>
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

export default connect(mapStateToProps, mapDispatchToProps)(CourseDishonestyCharts)