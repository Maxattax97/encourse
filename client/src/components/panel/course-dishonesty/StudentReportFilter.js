import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Dropdown, Filter} from "../../Helpers"
import StudentReportSummary from "./StudentReportSummary"

class StudentReportFilter extends Component {

	sort_by_ranges = ['Name', 'Score']
	order_ranges = ['Ascending', 'Descending']

	constructor(props) {
		super(props)

		this.state = {
			sort_by: 0,
			order_by: 0
		}
	}

	render() {
		return (
			<div className='course-students-report'>
				{
					this.props.students && this.props.students.length > 0 ?
						<Filter>
							<Dropdown header={<h5>Sort by { this.sort_by_ranges[this.state.sort_by] }</h5>}
							          onClick={ (index) => { this.setState({ sort_by: index }) }}
							          leftAnchor>

								{
									this.sort_by_ranges.map(range =>
										<h5 key={range}>
											Sort by {range}
										</h5>
									)
								}
							</Dropdown>
							<Dropdown header={<h5>{ this.order_ranges[this.state.order_by] } Order</h5>}
							          onClick={ (index) => { this.setState({ order_by: index }) }}
							          rightAnchor>
								{
									this.order_ranges.map(range =>
										<h5 key={range}>
											{range} Order
										</h5>
									)
								}
							</Dropdown>
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