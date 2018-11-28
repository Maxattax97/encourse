import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import { connect } from 'react-redux'

import ProjectTestSummary from "./ProjectTestSummary"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {resetFilterState, setFilterState, toggleSelectAllCards} from "../../../redux/actions"

class ProjectTestFilter extends Component {

	sort_by_values = ['Name', 'Points']
	bundle_by_values = ['Nothing', 'Visibility', 'Points', 'Test Suite']
	order_values = ['Ascending', 'Descending']
	view_values = ['All Tests', 'Visible Tests', 'Hidden Tests']

	render() {
		return (
			<div className='project-tests'>
				<Filter>
					<Checkbox onClick={() => this.props.toggleSelectAllCards()}>
						{
							this.props.selectedAllTests ?
								<CheckmarkIcon/>
								: null
						}
					</Checkbox>

					<Dropdown header='h5'
					          text='Sort by'
					          values={this.sort_by_values}
					          currentIndex={this.props.filters.sort_by}
					          onClick={ (index) => this.props.setFilterState('sort_by', index) }
					          left/>

					<Dropdown header='h5'
					          text='Bundle by'
					          values={this.bundle_by_values}
					          currentIndex={this.props.filters.bundle_by}
					          onClick={ (index) => this.props.setFilterState('bundle_by', index) }
					          left/>

					<Dropdown header='h5'
					          text='Order'
					          values={this.order_values}
					          currentIndex={this.props.filters.order_by}
					          onClick={ (index) => this.props.setFilterState('order_by', index) }
					          right />

					<Dropdown header='h5'
					          text='View'
					          values={this.view_values}
					          currentIndex={this.props.filters.view_filter}
					          onClick={ (index) => this.props.setFilterState('view_filter', index) }
					          left/>
				</Filter>
				<ProjectTestSummary />
			</div>
		)
	}
}


const mapStateToProps = (state) => {
	return {
		selectedAllTests: getAllSelected(state, 'tests'),
		filters: getFilters(state)
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
		toggleSelectAllCards: () => dispatch(toggleSelectAllCards('tests')),
		setFilterState: (id, value) => dispatch(setFilterState(id, value)),
		resetFilterState: () => dispatch(resetFilterState())
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectTestFilter)