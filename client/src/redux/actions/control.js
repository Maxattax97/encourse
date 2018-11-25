
export function setModalState(id) {
    return {
        type: 'SET_MODAL_STATE',
        id
    }
}

export function toggleSelectAllCards(id) {
	return {
		type: 'TOGGLE_SELECT_ALL_CARDS_OF_TYPE',
		id
	}
}

export function toggleSelectCard(id, index) {
    return {
        type: 'TOGGLE_SELECT_CARD_OF_TYPE',
        id,
        index
    }
}

export function resetAllCards(id) {
    return {
        type: 'RESET_ALL_CARDS_OF_TYPE',
        id
    }
}