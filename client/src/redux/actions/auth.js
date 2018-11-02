import genericDispatch from './fetch'

export function logInHasError(hasError) {
    return {
        type: 'LOG_IN_HAS_ERROR',
        hasError
    }
}

export function logInDataSuccess(data) {
    return {
        type: 'LOG_IN_DATA_SUCCESS',
        data
    }
}

export const logIn = genericDispatch(
    'LOG_IN', logInHasError, logInDataSuccess, 'POST'
)

export function logOutHasError(hasError) {
    return {
        type: 'LOG_OUT_HAS_ERROR',
        hasError
    }
}

export function logOutDataSuccess(data) {
    return {
        type: 'LOG_OUT_DATA_SUCCESS',
        data
    }
}

export const logOut = genericDispatch(
    'LOG_OUT', logOutHasError, logOutDataSuccess, 'GET'
)

export function changePasswordHasError(hasError) {
    return {
        type: 'CHANGE_PASSWORD_HAS_ERROR',
        hasError
    }
}

export function changePasswordDataSuccess(data) {
    return {
        type: 'CHANGE_PASSWORD_DATA_SUCCESS',
        data
    }
}

export const changePassword = genericDispatch(
    'CHANGE_PASSWORD', changePasswordHasError, changePasswordDataSuccess, 'POST'
)

export function getAccountHasError(hasError) {
    return {
        type: 'GET_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function getAccountDataSuccess(data) {
    return {
        type: 'GET_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const getAccount = genericDispatch(
    'GET_ACCOUNT', getAccountHasError, getAccountDataSuccess, 'GET'
)