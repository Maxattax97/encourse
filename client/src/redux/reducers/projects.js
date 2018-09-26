function projects(state = {}, action) {

    switch(action.type) {
        case 'SET_CURRENT_PROJECT':
            return Object.assign({}, state, {
                currentProject: action.project,
            })
        default:
            return state
    }
}

export default projects