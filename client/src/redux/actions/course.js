import genericDispatch from './fetch'

export function getStudentPreviewsHasError(hasError) {
    return {
        type: 'GET_STUDENT_PREVIEWS_HAS_ERROR',
        hasError
    }
}

export function getStudentPreviewsIsLoading(isLoading) {
    return {
        type: 'GET_STUDENT_PREVIEWS_IS_LOADING',
        isLoading
    }
}

export function getStudentPreviewsDataSuccess(data) {
    return {
        type: 'GET_STUDENT_PREVIEWS_DATA_SUCCESS',
        data
    }
}

export const getStudentPreviews = genericDispatch(
    getStudentPreviewsHasError, getStudentPreviewsIsLoading, getStudentPreviewsDataSuccess, 'GET'
)

export function getClassProjectsHasError(hasError) {
    return {
        type: 'GET_CLASS_PROJECTS_HAS_ERROR',
        hasError
    }
}

export function getClassProjectsIsLoading(isLoading) {
    return {
        type: 'GET_CLASS_PROJECTS_IS_LOADING',
        isLoading
    }
}

export function getClassProjectsDataSuccess(data) {
    return {
        type: 'GET_CLASS_PROJECTS_DATA_SUCCESS',
        data
    }
}

export const getClassProjects = genericDispatch(
    getClassProjectsHasError, getClassProjectsIsLoading, getClassProjectsDataSuccess, 'GET'
)

export function getClassProgressHasError(hasError) {
    return {
        type: 'GET_CLASS_PROGRESS_HAS_ERROR',
        hasError
    }
}

export function getClassProgressIsLoading(isLoading) {
    return {
        type: 'GET_CLASS_PROGRESS_IS_LOADING',
        isLoading
    }
}

export function getClassProgressDataSuccess(data) {
    return {
        type: 'GET_CLASS_PROGRESS_DATA_SUCCESS',
        data
    }
}

export const getClassProgress = genericDispatch(
    getClassProgressHasError, getClassProgressIsLoading, getClassProgressDataSuccess, 'GET'
)

