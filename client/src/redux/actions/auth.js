import {genericDispatch} from "./fetch"

const class_type = 'AUTH'

export const logIn = genericDispatch(
    class_type, 'LOG_IN', 'POST'
)

export const authenticateToken = genericDispatch(
    class_type, 'AUTHENTICATE_TOKEN', 'GET'
)


export const logOut = genericDispatch(
    class_type, 'LOG_OUT', 'GET'
)

export const changePassword = genericDispatch(
    class_type, 'CHANGE_PASSWORD', 'POST'
)

export const getAccount = genericDispatch(
    class_type, 'GET_ACCOUNT', 'GET'
)

export function setLocation(location) {
    return {
        class: class_type,
        type: 'SET_LOCATION',
        location
    }
}

export function setTokens(token) {
    return {
        class: class_type,
        type: 'SET_TOKENS',
        token
    }
}