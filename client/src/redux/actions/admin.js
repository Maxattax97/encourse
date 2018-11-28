import { genericDispatch } from "./fetch"

const class_type = 'ADMIN'

export const getCourses = genericDispatch(
    class_type, 'GET_COURSES', 'GET'
)

export const addCourse = genericDispatch(
   class_type, 'ADD_COURSE', 'POST'
)

export const modifyCourse = genericDispatch(
    class_type, 'MODIFY_COURSE', 'POST'
)

export const removeCourse = genericDispatch(
    class_type, 'REMOVE_COURSE', 'DELETE'
)

export const getAccounts = genericDispatch(
    class_type, 'GET_ACCOUNTS', 'GET'
)

export const addAccount = genericDispatch(
    class_type, 'ADD_ACCOUNT', 'POST'
)

export const modifyAccount = genericDispatch(
    class_type, 'MODIFY_ACCOUNT', 'POST'
)

export const removeAccount = genericDispatch(
    class_type, 'REMOVE_ACCOUNT', 'DELETE'
)