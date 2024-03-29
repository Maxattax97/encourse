import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import { connect } from 'react-redux'

import ProjectTestSummary from "./ProjectTestSummary"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {resetFilterState, toggleSelectAllCards} from "../../../redux/actions"

class ProjectTestFilter extends Component {

	sort_by_values = ['Name', 'Points']
	order_values = ['Ascending', 'Descending']
	view_values = ['All Tests', 'Visible Tests', 'Hidden Tests']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

	render() {
		return (
			<div className='project-tests'>
                <h3 className='header'>Test Scripts</h3>
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
                              filter='sort_by'
					          left/>

					<Dropdown header='h5'
					          text='Order'
					          values={this.order_values}
                              filter='order_by'
					          right />

					<Dropdown header='h5'
					          text='View'
					          values={this.view_values}
                              filter='view_filter'
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
		resetFilterState: () => dispatch(resetFilterState())
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectTestFilter)