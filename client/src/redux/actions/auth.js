import genericDispatch from './fetch'

export function logOut() {
    return {
        type: 'LOG_OUT'
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
