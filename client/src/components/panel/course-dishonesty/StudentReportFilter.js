import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Dropdown, Filter} from '../../Helpers'
import StudentReportSummary from './StudentReportSummary'

class StudentReportFilter extends Component {

	sort_by_ranges = ['Name', 'Score']
	order_ranges = ['Ascending', 'Descending']

	render() {
	    return (
	        <div className='course-students-report'>
	            {
	                this.props.report && this.props.report.length > 0 ?
	                    <Filter>
	                        <Dropdown header='h5'
	                                  text='Sort by'
	                                  values={ this.sort_by_ranges }
	                                  current_index={this.props.filters.sort_by}
							          onClick={ (index) => this.props.onChange('sort_by', index) }
							          left />

	                        <Dropdown header='h5'
	                                  text='Order'
	                                  values={ this.order_ranges }
	                                  current_index={ this.props.filters.order_by }
							          onClick={ (index) => this.props.onChange('order_by', index) }
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
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : []
    }
}


export default connect(mapStateToProps, null)(StudentReportFilter)