export function getAllSelected(state, type) {
	return state.control && state.control[type] ? state.control[type].selectedAll === 2 : false
}

const selectedIfPresent = (selected) => (id) => selected[id]
const selectedIfNotPresent = (selected) => (id) => !selected[id]

export function getSelected(state, type) {
	if(!state.control || !state.control[type])
		return selectedIfPresent({})

	return !state.control[type].selectedAll ?
		selectedIfPresent(state.control[type].selected) :
		selectedIfNotPresent(state.control[type].selected)
}

export function isAnySelected(state, type) {
	return state.control && state.control[type] ? state.control[type].selectedAll === 2 || Object.keys(state.control[type].selected).length : false
}

export function getFilters(state) {
	return state.control && state.control.filters ? state.control.filters : {}
}

export function getFilterModalType(state) {
    return state.control && state.control.filterModal ? state.control.filterModal : null
}