function setModalState(state, action) {
	return Object.assign({}, state, {
		isModalFocused: false,
		getCurrentModal: action.id || 0
	})
}

function toggleSelectAllCards(state, action) {
	const name = action.id.charAt(0).toUpperCase() + action.id.slice(1)

	return Object.assign({}, state, {
		['selectedAll' + name]: state['selectedAll' + name] === 2 ? 0 : 2,
		['selected' + name]: {}
	})
}

function toggleSelectCard(state, action) {
	const name = action.id.charAt(0).toUpperCase() + action.id.slice(1)

	if(!state['selected' + name])
		return Object.assign({}, state, {
			['selected' + name]: {[action.index]: true},
			['selectedAll' + name]: state['selectedAll' + name] === 2 ? 1 : 0
		})

	if(state['selected' + name][action.index]) {
		const newSelected = Object.assign({}, state['selected' + name])
		delete newSelected[action.index]

		return Object.assign({}, state, {
			['selected' + name]: newSelected,
			['selectedAll' + name]: state['selectedAll' + name] === 0 ? 0 : Object.keys(newSelected).length === 0 ? 2 : 1
		})
	}

	return Object.assign({}, state, {
		['selected' + name]: Object.assign({[action.index]: true}, state['selected' + name]),
		['selectedAll' + name]: state['selectedAll' + name] === 0 ? 0 : 1
	})
}

function resetAllCards(state, action) {
	const name = action.id.charAt(0).toUpperCase() + action.id.slice(1)

	return Object.assign({}, state, {
		['selectedAll' + name]: 0,
		['selected' + name]: {}
	})
}

function setFilterState(state, action) {
	return Object.assign({}, state, {
		filters: Object.assign({}, state.filters, {
			[action.id]: action.value
		})
	})
}

function resetFilterState(state, action) {
	return Object.assign({}, state, {
		filters: {}
	})
}

export default function control(state = {}, action) {
	if(action.type !== 'CONTROL')
		return state

	switch(action.class) {
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
			return Object.assign({}, state, {
				reduxError: action
			})
	}
}