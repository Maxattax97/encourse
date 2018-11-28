const class_type = 'CONTROL'

export function setModalState(id) {
    return {
    	class: class_type,
        type: 'SET_MODAL_STATE',
        id
    }
}

export function toggleSelectAllCards(id) {
	return {
		class: class_type,
		type: 'TOGGLE_SELECT_ALL_CARDS_OF_TYPE',
		id
	}
}

export function toggleSelectCard(id, index) {
    return {
	    class: class_type,
        type: 'TOGGLE_SELECT_CARD_OF_TYPE',
        id,
        index
    }
}

export function resetAllCards(id) {
    return {
	    class: class_type,
        type: 'RESET_ALL_CARDS_OF_TYPE',
        id
    }
}

export function setFilterState(id, value) {
	return {
		class: class_type,
		type: 'SET_FILTER_STATE',
		id,
		value
	}
}

export function resetFilterState() {
	return {
		class: class_type,
		type: 'RESET_FILTER_STATE'
	}
}