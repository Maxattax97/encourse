import {unknownAction} from './reducer-utils'

function setModalState(state, action) {
	return {
		...state,
		modalState: action.id || 0
	}
}

function toggleSelectAllCards(state, action) {
	return {
		...state,
		[action.id]: {
			selectedAll: state[action.id] && state[action.id].selectedAll === 2 ? 0 : 2,
			selected: {}
		}
	}
}

function toggleSelectCard(state, action) {
	if(!state[action.id])
		return {
			...state,
			[action.id]: {
				selectedAll: 0,
				selected: {[action.index]: true}
			}
		}

	if(state[action.id].selected[action.index]) {
		const newSelected = {
			...state[action.id].selected
		}
		delete newSelected[action.index]

		return {
			...state,
			[action.id]: {
				selectedAll: !state[action.id].selectedAll ? 0 : !Object.keys(newSelected).length ? 2 : 1,
				selected: newSelected
			}
		}
	}

	return {
		...state,
		[action.id]: {
			selectedAll: !state[action.id].selectedAll ? 0 : 1,
			selected: {
				...state[action.id].selected,
				[action.index]: true
			}
		}
	}
}

function resetAllCards(state, action) {
	return {
		...state,
		[action.id]: null
	}
}

function setFilterState(state, action) {
	return {
		...state,
		filters: {
			...state.filters,
			[action.id]: action.value
		}
	}
}

function resetFilterState(state, action) {
	return {
		...state,
		filters: {}
	}
}

export default function control(state = {}, action) {
	if(action.class !== 'CONTROL')
		return state

	switch(action.type) {
		case 'SET_MODAL_STATE':
			return setModalState(state, action)
		case 'TOGGLE_SELECT_ALL_CARDS_OF_TYPE':
			return toggleSelectAllCards(state, action)
		case 'TOGGLE_SELECT_CARD_OF_TYPE':
			return toggleSelectCard(state, action)
		case 'RESET_ALL_CARDS_OF_TYPE':
			return resetAllCards(state, action)
		case 'SET_FILTER_STATE':
			return setFilterState(state, action)
		case 'RESET_FILTER_STATE':
			return resetFilterState(state, action)
		default:
			return unknownAction(state, action)
	}
}