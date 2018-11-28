import {genericDispatch} from "./fetch"

const type = 'AUTH'

export const logIn = genericDispatch(
    type, 'LOG_IN', 'POST'
)

export const logOut = genericDispatch(
    type, 'LOG_OUT', 'GET'
)

export const changePassword = genericDispatch(
    type, 'CHANGE_PASSWORD', 'POST'
)

export const getAccount = genericDispatch(
    type, 'GET_ACCOUNT', 'GET'
)

export function setLocation(location) {
    return {
        type: 'SET_LOCATION',
        location
    }
}