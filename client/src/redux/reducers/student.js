function student(state = {}, action) {

    switch(action.type) {
        case 'SET_CURRENT_STUDENT':
            return Object.assign({}, state, {
                currentStudent: action.student,
            })
        default:
            return state
    }
}

export default student