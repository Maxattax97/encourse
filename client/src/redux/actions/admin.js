import { genericDispatch } from "./index"

const type = 'ADMIN'

export const getCourses = genericDispatch(
    type, 'GET_COURSES', 'GET'
)

export const addCourse = genericDispatch(
   type, 'ADD_COURSE', 'POST'
)

export const modifyCourse = genericDispatch(
    type, 'MODIFY_COURSE', 'POST'
)

export const removeCourse = genericDispatch(
    type, 'REMOVE_COURSE', 'DELETE'
)

export const getAccounts = genericDispatch(
    type, 'GET_ACCOUNTS', 'GET'
)

export const addAccount = genericDispatch(
    type, 'ADD_ACCOUNT', 'POST'
)

export const modifyAccount = genericDispatch(
    type, 'MODIFY_ACCOUNT', 'POST'
)

export const removeAccount = genericDispatch(
    type, 'REMOVE_ACCOUNT', 'DELETE'
)