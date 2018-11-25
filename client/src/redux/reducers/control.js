function control(state = {}, action) {
    let name
	switch(action.type) {
		case 'SET_MODAL_STATE':
			if(action.id)
				return Object.assign({}, state, {
					isModalFocused: true,
					getCurrentModal: action.id
				})
			else
				return Object.assign({}, state, {
					isModalFocused: false,
					getCurrentModal: 0
				})
		case 'TOGGLE_SELECT_ALL_CARDS_OF_TYPE':
		    name = action.id.charAt(0).toUpperCase() + action.id.slice(1)

			return Object.assign({}, state, {
				['selectedAll' + name]: state['selectedAll' + name] === 2 ? 0 : 2,
                ['selected' + name]: {}
			})
        case 'TOGGLE_SELECT_CARD_OF_TYPE':
	        name = action.id.charAt(0).toUpperCase() + action.id.slice(1)

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
        case 'RESET_ALL_CARDS_OF_TYPE':
	        name = action.id.charAt(0).toUpperCase() + action.id.slice(1)

	        return Object.assign({}, state, {
		        ['selectedAll' + name]: 0,
		        ['selected' + name]: {}
            })
		default:
			return state
	}
}

export default control