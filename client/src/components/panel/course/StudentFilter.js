import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import CourseStudentSummary from './StudentSummary'
import {toggleSelectAllCards, resetFilterState, setFilterState} from "../../../redux/actions"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {getStudents} from "../../../redux/state-peekers/course"
import {getCurrentProject} from '../../../redux/state-peekers/projects'

class StudentFilter extends Component {

    sort_by_values = ['Name', 'Hours', 'Commits', 'Progress']
    order_values = ['Ascending', 'Descending']
	view_values = ['All Tests', 'Visible Tests', 'Hidden Tests']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

    getFilterCommitValues = () => {
        let values = ['Any', '0-25%', '25-50%', '50-75%', '75-100%']
        /*if(this.props.students && this.props.students.page &&
            this.props.students.page.filters && this.props.students.page.filters.commitCounts) {
                const commitCounts = this.props.students.page.filters.commitCounts
                for(let i = 0; i < commitCounts.length - 1; i++) {
                    if(i === commitCounts.length - 2) {
                        values.push(`${commitCounts[i]}+`)
                    } else {
                        values.push(`${commitCounts[i]} - ${commitCounts[i+1]}`)
                    } 
                }

        }*/

        return values
    }

    getFilterTimeValues = () => {
        let values = ['Any', '0-25%', '25-50%', '50-75%', '75-100%']
        /*if(this.props.students && this.props.students.page &&
            this.props.students.page.filters && this.props.students.page.filters.timeSpent) {
                const timeSpent = this.props.students.page.filters.timeSpent
                for(let i = 0; i < timeSpent.length - 1; i++) {
                    if(i === timeSpent.length - 2) {
                        values.push(`${timeSpent[i]}+`)
                    } else {
                        values.push(`${timeSpent[i]} - ${timeSpent[i+1]}`)
                    } 
                }
        }*/

        return values
    }

    getFilterProgressValues = () => {
        let values = ['Any', '0-25%', '25-50%', '50-75%', '75-100%']
        /*if(this.props.students && this.props.students.page &&
            this.props.students.page.filters && this.props.students.page.filters.grades) {
                const grades = this.props.students.page.filters.grades
                for(let i = 0; i < grades.length - 1; i++) {
                    if(i === grades.length - 2) {
                        values.push(`${grades[i]}%+`)
                    } else {
                        values.push(`${grades[i]}% - ${grades[i+1]}%`)
                    } 
                }
        }*/

        return values
    }

	render() {
        return (
            <div className='course-students'>
                <h3 className='header'>Students { this.props.students.data.length ? '(' + this.props.students.data.length + ')' : '' }</h3>
                {/* {
                    !this.props.students.loading && !this.props.students.error ?
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

                            {
                                this.props.project && this.props.project.runTestall ?
                                    <Dropdown header='h5'
                                              text='View'
                                              values={this.view_values}
                                              filter='view_filter'
                                              left/>
                                    : null
                            }

                        </Filter>
                        :
                        null
                }*/}
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
        project: getCurrentProject(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
	    toggleSelectAllCards: () => dispatch(toggleSelectAllCards('students')),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentFilter)