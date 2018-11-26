import React, { Component } from 'react'
import {Checkbox, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import ProjectTestSummary from "./ProjectTestSummary"

class ProjectTestFilter extends Component {

	sort_by_values = ['Name', 'Points']
	bundle_by_values = ['Nothing', 'Visibility', 'Points', 'Test Suite']
	order_values = ['Ascending', 'Descending']
	view_values = ['All Tests', 'Visible Tests', 'Hidden Tests']

	render() {
		return (
			<div className='project-tests'>
				<Filter>
					<Checkbox>

					</Checkbox>

					<Dropdown header='h5'
					          text='Sort by'
					          values={this.sort_by_values}
					          currentIndex={this.props.filters.sort_by}
					          onClick={ (index) => this.props.onChange('sort_by', index) }
					          left/>

					<Dropdown header='h5'
					          text='Bundle by'
					          values={this.bundle_by_values}
					          currentIndex={this.props.filters.bundle_by}
					          onClick={ (index) => this.props.onChange('bundle_by', index) }
					          left/>

					<Dropdown header='h5'
					          text='Order'
					          values={this.order_values}
					          currentIndex={this.props.filters.order_by}
					          onClick={ (index) => this.props.onChange('order_by', index) }
					          right />

					<Dropdown header='h5'
					          text='View'
					          values={this.view_values}
					          currentIndex={this.props.filters.view_filter}
					          onClick={ (index) => this.props.onChange('view_filter', index) }
					          left/>
				</Filter>
				<ProjectTestSummary />
			</div>
		)
	}
}

export default connect(null, null)(ProjectTestFilter)