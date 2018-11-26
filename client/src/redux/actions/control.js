const type = 'CONTROL'

export function setModalState(id) {
    return {
    	type,
        class: 'SET_MODAL_STATE',
        id
    }
}

export function toggleSelectAllCards(id) {
	return {
		type,
		class: 'TOGGLE_SELECT_ALL_CARDS_OF_TYPE',
		id
	}
}

export function toggleSelectCard(id, index) {
    return {
	    type,
        class: 'TOGGLE_SELECT_CARD_OF_TYPE',
        id,
        index
    }
}

export function resetAllCards(id) {
    return {
	    type,
        class: 'RESET_ALL_CARDS_OF_TYPE',
        id
    }
}

export function setFilterState(id, value) {
	return {
		type,
		class: 'SET_FILTER_STATE',
		id,
		value
	}
}

export function resetFilterState() {
	return {
		type,
		class: 'RESET_FILTER_STATE'
	}
}