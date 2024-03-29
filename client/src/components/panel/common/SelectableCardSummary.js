import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Checkbox, CheckmarkIcon, Summary } from '../../Helpers'
import {
	resetAllCards,
	toggleSelectCard
} from '../../../redux/actions'
import PreviewCard from "../common/PreviewCard"
import { getAllSelected, getSelected, isAnySelected } from "../../../redux/state-peekers/control"

class SelectableCardSummary extends Component {

    componentWillUnmount() {
        this.props.resetAllCards(this.props.type)
    }

	isSelected = (value) => {
		return this.props.selectedAll || (this.props.selected(value.id))
	}

	clickCard = (value) => {
		if(this.props.isAnySelected && !this.props.noReset)
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
					this.props.values &&
					this.props.values.map && 
					this.props.values.map( (value) =>
						<PreviewCard onClick={ () => this.clickCard(value) } isSelected={ this.isSelected(value) } key={ value.id || value.studentID }>
							{ this.props.render(value) }
                            {
                                !this.props.noCheckmark ?
                                    <Checkbox className={ this.props.isAnySelected ? 'card-select selectable' : 'card-select' } onClick={ (e) => this.clickSelect(e, value) }>
                                        {
                                            this.isSelected(value) ?
                                                <CheckmarkIcon/>
                                                : null
                                        }
                                    </Checkbox>
                                    : null
                            }

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