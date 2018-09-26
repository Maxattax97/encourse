function auth(state = {}, action) {
    switch(action.type) {
        case 'LOG_IN_HAS_ERROR':
            return Object.assign({}, state, {
                logInHasError: action.hasError,
            })
        case 'LOG_IN_IS_LOADING':
            return Object.assign({}, state, {
                logInIsLoading: action.isLoading,
            })
        case 'LOG_IN_DATA_SUCCESS':
            return Object.assign({}, state, {
                logInData: action.data,
            })
        case 'LOG_OUT':
            return Object.assign({}, state, {
                logInData: null,
            })
        default:
            return state
    }
}

export default auth