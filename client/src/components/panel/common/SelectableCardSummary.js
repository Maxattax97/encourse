import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Checkbox, CheckmarkIcon, Summary, Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {history} from '../../../redux/store'
import {
	getStudentPreviews,
	resetAllCards,
	setCurrentStudent,
	toggleSelectAllCards,
	toggleSelectCard
} from '../../../redux/actions'
import PreviewCard from "../common/PreviewCard"
import {getAllSelected, getSelected, isAnySelected} from "../../../redux/state-peekers/control"

class SelectableCardSummary extends Component {

	isSelected = (value) => {
		return this.props.selectedAll || (this.props.selected(value.id))
	}

	clickCard = (value) => {
		if(this.isAnySelected())
			return this.props.resetAllCards(this.props.type)

		this.props.onClick(value)
	};

	clickSelect = (event, value) => {
		event.stopPropagation()

		this.props.toggleSelectCard(this.props.type, value.id)
	}

	render() {
		return (
			<Summary columns={ 5 }>
				{
					this.props.values.map( (value) =>
						<PreviewCard onClick={ () => this.clickCard(value) } isSelected={ this.isSelected(value) } key={ value.id }>
							{ this.props.render(value) }
							<Checkbox className={ this.props.isAnySelected ? 'card-select selectable' : 'card-select' } onClick={ (e) => this.clickSelect(e, value) }>
								{
									this.isSelected(value) ?
										<CheckmarkIcon/>
										: null
								}
							</Checkbox>
						</PreviewCard>
					)
				}
			</Summary>
		)
	}
}

const mapStateToProps = (state, props) => {
	return {
		selectedAll: getAllSelected(state, props.type),
		selected: getSelected(state, props.type),
		isAnySelected: isAnySelected(state, props.type)
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
		toggleSelectCard: (id, index) => dispatch(toggleSelectCard(id, index)),
		resetAllCards: (id) => dispatch(resetAllCards(id))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(SelectableCardSummary)