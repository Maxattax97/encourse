import genericDispatch from './fetch'

export function addCourseHasError(hasError) {
    return {
        type: 'ADD_COURSE_HAS_ERROR',
        hasError
    }
}

export function addCourseIsLoading(isLoading) {
    return {
        type: 'ADD_COURSE_IS_LOADING',
        isLoading
    }
}

export function addCourseDataSuccess(data) {
    return {
        type: 'ADD_COURSE_DATA_SUCCESS',
        data
    }
}

export const addCourse = genericDispatch(
    addCourseHasError, addCourseIsLoading, addCourseDataSuccess, 'POST'
)

export function modifyCourseHasError(hasError) {
    return {
        type: 'MODIFY_COURSE_HAS_ERROR',
        hasError
    }
}

export function modifyCourseIsLoading(isLoading) {
    return {
        type: 'MODIFY_COURSE_IS_LOADING',
        isLoading
    }
}

export function modifyCourseDataSuccess(data) {
    return {
        type: 'MODIFY_COURSE_DATA_SUCCESS',
        data
    }
}

export const modifyCourse = genericDispatch(
    modifyCourseHasError, modifyCourseIsLoading, modifyCourseDataSuccess, 'POST'
)

export function removeCourseHasError(hasError) {
    return {
        type: 'REMOVE_COURSE_HAS_ERROR',
        hasError
    }
}

export function removeCourseIsLoading(isLoading) {
    return {
        type: 'REMOVE_COURSE_IS_LOADING',
        isLoading
    }
}

export function removeCourseDataSuccess(data) {
    return {
        type: 'REMOVE_COURSE_DATA_SUCCESS',
        data
    }
}

export const removeCourse = genericDispatch(
    removeCourseHasError, removeCourseIsLoading, removeCourseDataSuccess, 'DELETE'
)

export function addAccountHasError(hasError) {
    return {
        type: 'ADD_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function addAccountIsLoading(isLoading) {
    return {
        type: 'ADD_ACCOUNT_IS_LOADING',
        isLoading
    }
}

export function addAccountDataSuccess(data) {
    return {
        type: 'ADD_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const addAccount = genericDispatch(
    addAccountHasError, addAccountIsLoading, addAccountDataSuccess, 'POST'
)

export function modifyAccountHasError(hasError) {
    return {
        type: 'MODIFY_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function modifyAccountIsLoading(isLoading) {
    return {
        type: 'MODIFY_ACCOUNT_IS_LOADING',
        isLoading
    }
}

export function modifyAccountDataSuccess(data) {
    return {
        type: 'MODIFY_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const modifyAccount = genericDispatch(
    modifyAccountHasError, modifyAccountIsLoading, modifyAccountDataSuccess, 'POST'
)

export function removeAccountHasError(hasError) {
    return {
        type: 'REMOVE_ACCOUNT_HAS_ERROR',
        hasError
    }
}

export function removeAccountIsLoading(isLoading) {
    return {
        type: 'REMOVE_ACCOUNT_IS_LOADING',
        isLoading
    }
}

export function removeAccountDataSuccess(data) {
    return {
        type: 'REMOVE_ACCOUNT_DATA_SUCCESS',
        data
    }
}

export const removeAccount = genericDispatch(
    removeAccountHasError, removeAccountIsLoading, removeAccountDataSuccess, 'POST'
)