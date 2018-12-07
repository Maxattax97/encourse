import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import StudentReportSummary from './StudentReportSummary'
import {getStudents} from "../../../redux/state-peekers/course"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {resetFilterState, setFilterState, toggleSelectAllCards} from "../../../redux/actions"

class StudentReportFilter extends Component {

	sort_by_ranges = ['Name', 'Score']
	order_ranges = ['Ascending', 'Descending']

	componentWillUnmount() {
		this.props.resetFilterState()
	}

	render() {
	    return (
	        <div className='course-students-report'>
                <h3 className='header'>Students</h3>
	            {
	                this.props.report.length ?
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
	                                  values={ this.sort_by_ranges }
                                      filter='sort_by'
							          left />

	                        <Dropdown header='h5'
	                                  text='Order'
	                                  values={ this.order_ranges }
                                      filter='order_by'
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

export default connect(mapStateToProps, mapDispatchToProps)(StudentReportFilter)