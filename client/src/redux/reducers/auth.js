function auth(state = {}, action) {
    switch(action.type) {
    case 'LOG_IN':
        return Object.assign({}, state, {
            logInIsLoading: true,
        })    
    case 'LOG_IN_HAS_ERROR':
        return Object.assign({}, state, {
            logInHasError: action.hasError,
            logInIsLoading: false,
        })
    case 'SET_TOKENS':
    case 'LOG_IN_DATA_SUCCESS':
        const expires_in = action.data.expires_in
        const expires_at = Date.now() + expires_in*1000
        return Object.assign({}, state, {
            logInData: {...action.data, expires_at},
            logInIsLoading: false,
        })
    case 'LOG_OUT':
        return Object.assign({}, state, {
            logInData: null,
            logOutIsLoading: true,
        })   
    case 'LOG_OUT_HAS_ERROR':
        return Object.assign({}, state, {
            logOutHasError: action.hasError,
            logOutIsLoading: false,
        })
    case 'LOG_OUT_DATA_SUCCESS':
        return Object.assign({}, state, {
            logInData: null,
            logOutIsLoading: false,
        })
    case 'CHANGE_PASSWORD':
        return Object.assign({}, state, {
            changePasswordIsLoading: true,
        })
    case 'CHANGE_PASSWORD_HAS_ERROR':
        return Object.assign({}, state, {
            changePasswordHasError: action.hasError,
            changePasswordIsLoading: false,
        })
    case 'CHANGE_PASSWORD_DATA_SUCCESS':
        return Object.assign({}, state, {
            changePasswordData: action.data,
            changePasswordIsLoading: false,
        })
    case 'GET_ACCOUNT':
        return Object.assign({}, state, {
            getAccountIsLoading: true,
        })
    case 'GET_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            getAccountHasError: action.hasError,
            getAccountIsLoading: false,
        })
    case 'GET_ACCOUNT_DATA_SUCCESS':
        return Object.assign({}, state, {
            getAccountData: action.data,
            getAccountIsLoading: false,
        })
    default:
        return state
    }
}

export default auth