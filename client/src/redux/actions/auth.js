import genericDispatch from './fetch'

export function setToken(token) {
    return {
        type: 'SET_TOKEN',
        token
    }
}

export function logInHasError(hasError) {
    return {
        type: 'LOG_IN_HAS_ERROR',
        hasError
    }
}

export function logInIsLoading(isLoading) {
    return {
        type: 'LOG_IN_IS_LOADING',
        isLoading
    }
}

export function logInDataSuccess(data) {
    return {
        type: 'LOG_IN_DATA_SUCCESS',
        data
    }
}

export const logIn = genericDispatch(
    logInHasError, logInIsLoading, logInDataSuccess, 'POST'
)

export function logOutHasError(hasError) {
    return {
        type: 'LOG_OUT_HAS_ERROR',
        hasError
    }
}

export function logOutIsLoading(isLoading) {
    return {
        type: 'LOG_OUT_IS_LOADING',
        isLoading
    }
}

export function logOutDataSuccess(data) {
    return {
        type: 'LOG_OUT_DATA_SUCCESS',
        data
    }
}

export const logOut = genericDispatch(
    logOutHasError, logOutIsLoading, logOutDataSuccess, 'GET'
)

export function logOutClient() {
    return {
        type: 'LOG_OUT'
    }
}


