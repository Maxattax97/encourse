import genericDispatch from './fetch'

export function setCurrentProject(id, index) {
    return {
        type: 'SET_CURRENT_PROJECT',
        id,
        index,
    }
}

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