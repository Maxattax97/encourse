function modal(state = {}, action) {
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
    default:
        return state
    }
}

export default modal