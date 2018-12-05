import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import {getStudents} from "../../../redux/state-peekers/course"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {resetFilterState, setFilterState, toggleSelectAllCards} from "../../../redux/actions"

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
                                      currentIndex={this.props.filters.bundle_by}
                                      onClick={ (index) => this.props.setFilterState('bundle_by', index) }
                                      left />

                            <Dropdown header='h5'
                                      text='Order'
                                      values={ this.order_values }
                                      currentIndex={ this.props.filters.order_by }
                                      onClick={ (index) => this.props.setFilterState('order_by', index) }
                                      right />
                        </Filter>
                        :
                        null
                }

                <StudentReportSummary/>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        selectedAllCourses: getAllSelected(state, 'courses'),
        filters: getFilters(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleSelectAllCards: () => dispatch(toggleSelectAllCards('courses')),
        setFilterState: (id, value) => dispatch(setFilterState(id, value)),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursesFilter)