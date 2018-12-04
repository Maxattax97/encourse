import React, { Component } from 'react'
import { connect } from 'react-redux'

import CoursesSummary from "./course-selection/CoursesSummary"

class CourseSelectionPanel extends Component {

	render() {

		return (
			<div className='panel-course'>

				<div className='panel-center-content'>

					<div className='panel-course-content'>

						<h3 className='header'>Course Selection</h3>
						<CoursesSummary/>
					</div>
				</div>
			</div>
		)
	}
}

const mapStateToProps = (state) => {
	return {

	}
}

const mapDispatchToProps = (dispatch) => {
	return {

	}
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CourseSelectionPanel)
