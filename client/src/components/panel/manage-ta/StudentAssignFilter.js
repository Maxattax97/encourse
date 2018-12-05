import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import StudentAssignSummary from './StudentAssignSummary'
import {toggleSelectAllCards, resetFilterState, setFilterState} from "../../../redux/actions"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {getStudents} from "../../../redux/state-peekers/course"

class StudentAssignFilter extends Component {

    order_values = ['Ascending', 'Descending']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

	render() {
        return (
            <div className='course-students'>
                <h3 className='header'>Assign Students</h3>
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
                                      text='Order'
                                      values={this.order_values}
                                      currentIndex={this.props.filters.order_by}
                                      onClick={ (index) => this.props.setFilterState('order_by', index) }
                                      right />

                        </Filter>
                        :
                        null
                }
                <StudentAssignSummary />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        selectedAllStudents: getAllSelected(state, 'students'),
        filters: getFilters(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
	    toggleSelectAllCards: () => dispatch(toggleSelectAllCards('students')),
        setFilterState: (id, value) => dispatch(setFilterState(id, value)),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentAssignFilter)