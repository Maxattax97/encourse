import React, { Component } from 'react'

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

export default CourseSelectionPanel
