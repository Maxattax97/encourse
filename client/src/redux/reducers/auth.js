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
        const expires_in = action.data.expires_in
        const expires_at = Date.now() + expires_in*1000
        return Object.assign({}, state, {
            logInData: {...action.data, expires_at},
        })
    case 'LOG_OUT_HAS_ERROR':
        return Object.assign({}, state, {
            logOutHasError: action.hasError,
        })
    case 'LOG_OUT_IS_LOADING':
        return Object.assign({}, state, {
            logInData: null,
            logOutIsLoading: action.isLoading,
        })
    case 'LOG_OUT_DATA_SUCCESS':
        return Object.assign({}, state, {
            logInData: null,
        })
    case 'CHANGE_PASSWORD_HAS_ERROR':
        return Object.assign({}, state, {
            changePasswordHasError: action.hasError,
        })
    case 'CHANGE_PASSWORD_IS_LOADING':
        return Object.assign({}, state, {
            changePasswordIsLoading: action.isLoading,
        })
    case 'CHANGE_PASSWORD_DATA_SUCCESS':
        return Object.assign({}, state, {
            changePasswordData: action.data,
        })
    default:
        return state
    }
}

export default auth