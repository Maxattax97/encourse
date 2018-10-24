import genericDispatch from './fetch'

export function getCoursesHasError(hasError) {
    return {
        type: 'GET_COURSES_HAS_ERROR',
        hasError
    }
}

export function getCoursesDataSuccess(data) {
    return {
        type: 'GET_COURSES_DATA_SUCCESS',
        data
    }
}

export const getCourses = genericDispatch(
    'GET_COURSES', getCoursesHasError, getCoursesDataSuccess, 'GET'
)

export function addCourseHasError(hasError) {
    return {
        type: 'ADD_COURSE_HAS_ERROR',
        hasError
    }
}

export function addCourseDataSuccess(data) {
    return {
        type: 'ADD_COURSE_DATA_SUCCESS',
        data
    }
}

export const addCourse = genericDispatch(
   'ADD_COURSE', addCourseHasError, addCourseDataSuccess, 'POST'
)

export function modifyCourseHasError(hasError) {
    return {
        type: 'MODIFY_COURSE_HAS_ERROR',
        hasError
    }
}

export function modifyCourseDataSuccess(data) {
    return {
        type: 'MODIFY_COURSE_DATA_SUCCESS',
        data
    }
}

export const modifyCourse = genericDispatch(
    'MODIFY_COURSE', modifyCourseHasError, modifyCourseDataSuccess, 'POST'
)

export function removeCourseHasError(hasError) {
    return {
        type: 'REMOVE_COURSE_HAS_ERROR',
        hasError
    }
}

export function removeCourseDataSuccess(data) {
    return {
        type: 'REMOVE_COURSE_DATA_SUCCESS',
        data
    }
}

export const removeCourse = genericDispatch(
    'REMOVE_COURSE', removeCourseHasError, removeCourseDataSuccess, 'DELETE'
)

export function getAccountsHasError(hasError) {
    return {
        type: 'GET_ACCOUNTS_HAS_ERROR',
        hasError
    }
}

export function getAccountsDataSuccess(data) {
    return {
        type: 'GET_ACCOUNTS_DATA_SUCCESS',
        data
    }
}

export const getAccounts = genericDispatch(
    'GET_ACCOUNTS', getAccountsHasError, getAccountsDataSuccess, 'GET'
)

export function addAccountHasError(hasError) {
    return {
        type: 'ADD_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function addAccountDataSuccess(data) {
    return {
        type: 'ADD_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const addAccount = genericDispatch(
    'ADD_ACCOUNT', addAccountHasError, addAccountDataSuccess, 'POST'
)

export function modifyAccountHasError(hasError) {
    return {
        type: 'MODIFY_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function modifyAccountDataSuccess(data) {
    return {
        type: 'MODIFY_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const modifyAccount = genericDispatch(
    'MODIFY_ACCOUNT', modifyAccountHasError, modifyAccountDataSuccess, 'POST'
)

export function removeAccountHasError(hasError) {
    return {
        type: 'REMOVE_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function removeAccountDataSuccess(data) {
    return {
        type: 'REMOVE_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const removeAccount = genericDispatch(
    'REMOVE_ACCOUNT', removeAccountHasError, removeAccountDataSuccess, 'DELETE'
)