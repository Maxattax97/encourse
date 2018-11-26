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

function formatName(name) {
	console.log(name)
	return name.charAt(0).toUpperCase() + name.splice(1)
}

class SelectableCardSummary extends Component {

	constructor(props) {
		super(props)

		this.selectedAll = 'selectedAll' + this.props.type.charAt(0).toUpperCase() + this.props.type.substring(1)
		this.selected = 'selected' + this.props.type.charAt(0).toUpperCase() + this.props.type.substring(1)
	}

	isAnySelected = () => {
		return !this.props.control.invalid && this.props.control[this.selectedAll] > 0 || (this.props.control[this.selected] && Object.keys(this.props.control[this.selected]).length > 0)
	}

	isSelected = (value) => {
		return !this.props.control.invalid && (this.props.control[this.selectedAll] === 2 ||
			((this.props.control[this.selectedAll] === 0) === (this.props.control[this.selected] && this.props.control[this.selected][value.id] === true)))
	}

	clickCard = (value) => {
		if(this.isAnySelected())
			return this.props.resetAllCards()

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
							<Checkbox className={ this.isAnySelected() ? 'card-select selectable' : 'card-select' } onClick={ (e) => this.clickSelect(e, value) }>
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

const mapStateToProps = (state) => {
	return {
		control: state.control ? state.control : {invalid: true},
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
		toggleSelectCard: (id, index) => dispatch(toggleSelectCard(id, index)),
		resetAllCards: (id) => dispatch(resetAllCards(id))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(SelectableCardSummary)