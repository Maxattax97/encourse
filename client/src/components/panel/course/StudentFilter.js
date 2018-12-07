import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import CourseStudentSummary from './StudentSummary'
import {toggleSelectAllCards, resetFilterState, setFilterState} from "../../../redux/actions"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {getStudents} from "../../../redux/state-peekers/course"

class StudentFilter extends Component {

    sort_by_values = ['Name', 'Hours', 'Commits', 'Progress']
    order_values = ['Ascending', 'Descending']
	view_values = ['All Tests', 'Visible Tests', 'Hidden Tests']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

    componentDidUpdate() {
        console.log(this.props.students)
    }

    getFilterCommitValues = () => {
        let values = ['Any']
        if(this.props.students && this.props.students.page && 
            this.props.students.page.filters && this.props.students.page.filters.commitCounts) {
                const commitCounts = this.props.students.page.filters.commitCounts
                values =  ['Any', `${commitCounts[0]} - ${commitCounts[1]}`, 
                                  `${commitCounts[1]+1} - ${commitCounts[2]}`,
                                  `${commitCounts[2]+1} - ${commitCounts[3]}`,
                                  `${commitCounts[3]+1}+`]
        }

        return values
    }

    getFilterTimeValues = () => {
        let values = ['Any']
        if(this.props.students && this.props.students.page && 
            this.props.students.page.filters && this.props.students.page.filters.timeSpent) {
                const timeSpent = this.props.students.page.filters.timeSpent
                values =  ['Any', `${timeSpent[0]} - ${timeSpent[1]}`, 
                                  `${timeSpent[1]+1} - ${timeSpent[2]}`,
                                  `${timeSpent[2]+1} - ${timeSpent[3]}`,
                                  `${timeSpent[3]+1}+`]
        }

        return values
    }

    getFilterProgressValues = () => {
        let values = ['Any']
        if(this.props.students && this.props.students.page && 
            this.props.students.page.filters && this.props.students.page.filters.grades) {
                const grades = this.props.students.page.filters.grades
                values =  ['Any', `${grades[0]}% - ${grades[1]}%`, 
                                  `${grades[1]+1}% - ${grades[2]}%`,
                                  `${grades[2]+1}% - ${grades[3]}%`,
                                  `${grades[3]+1}%+`]
        }

        return values
    }

	render() {
        return (
            <div className='course-students'>
                <h3 className='header'>Students</h3>
                {
                    this.props.students.data ?
                        <Filter>
	                        <Checkbox onClick={() => this.props.toggleSelectAllCards()}>
                                {
                                    this.props.selectedAllStudents ?
                                        <CheckmarkIcon/>
                                        : null
                                }
	                        </Checkbox>

	                        <Dropdown header='h5'
	                                  text='Sort by'
	                                  values={this.sort_by_values}
                                      filter='sort_by'
	                                  left/>

                            <Dropdown header='h5'
                                      text='Order'
                                      values={this.order_values}
                                      filter='order_by'
                                      right />

                            <Dropdown header='h5'
                                      text='Commits'
                                      values={this.getFilterCommitValues()}
                                      filter='commit_filter'
                                      addCustom
                                      right />

                            <Dropdown header='h5'
                                      text='Hours'
                                      values={this.getFilterTimeValues()}
                                      filter='hour_filter'
                                      addCustom
                                      right/>

                            <Dropdown header='h5'
                                      text='Progress'
                                      values={this.getFilterProgressValues()}
                                      filter='progress_filter'
                                      addCustom
                                      right />

	                        <Dropdown header='h5'
	                                  text='View'
	                                  values={this.view_values}
                                      filter='view_filter'
	                                  left/>
                        </Filter>
                        :
                        null
                }
                <CourseStudentSummary />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        selectedAllStudents: getAllSelected(state, 'students'),
        filters: getFilters(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
	    toggleSelectAllCards: () => dispatch(toggleSelectAllCards('students')),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentFilter)