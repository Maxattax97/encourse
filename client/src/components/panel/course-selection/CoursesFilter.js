import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import {getStudents} from "../../../redux/state-peekers/course"
import {getAllSelected} from "../../../redux/state-peekers/control"
import {resetFilterState, toggleSelectAllCards} from "../../../redux/actions"
import CoursesSummary from './CoursesSummary'

class CoursesFilter extends Component {

    bundle_by_values = ['Title', 'Semester']
    order_values = ['Ascending', 'Descending']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

    render() {
        return (
            <div className='course-students-report'>
                {
                    this.props.report.length ?
                        <Filter>
                            <Checkbox onClick={() => this.props.toggleSelectAllCards()}>
                                {
                                    this.props.selectedAllCourses ?
                                        <CheckmarkIcon/>
                                        : null
                                }
                            </Checkbox>

                            <Dropdown header='h5'
                                      text='Bundle by'
                                      values={ this.bundle_by_values }
                                      filter='bundle_by'
                                      left />

                            <Dropdown header='h5'
                                      text='Order'
                                      values={ this.order_values }
                                      filter='order_by'
                                      right />
                        </Filter>
                        :
                        null
                }

                <CoursesSummary/>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        selectedAllCourses: getAllSelected(state, 'courses'),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleSelectAllCards: () => dispatch(toggleSelectAllCards('courses')),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursesFilter)