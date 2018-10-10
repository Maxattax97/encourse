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
        localStorage.setItem('token', JSON.stringify(action.data))
        return Object.assign({}, state, {
            logInData: action.data,
        })
    case 'LOG_OUT_HAS_ERROR':
        return Object.assign({}, state, {
            logOutHasError: action.hasError,
        })
    case 'LOG_OUT_IS_LOADING':
        return Object.assign({}, state, {
            logOutIsLoading: action.isLoading,
        })
    case 'LOG_OUT_DATA_SUCCESS':
        if(localStorage.getItem('token') != null) localStorage.removeItem('token')
        return Object.assign({}, state, {
            logInData: null,
        })
    case 'SET_TOKEN':
        return Object.assign({}, state, {
            logInData: action.token,
        })
    case 'LOG_OUT':
        if(localStorage.getItem('token') != null) localStorage.removeItem('token')
        return Object.assign({}, state, {
            logInData: null,
            logOutHasError: false,
        })
    default:
        return state
    }
}

export default auth