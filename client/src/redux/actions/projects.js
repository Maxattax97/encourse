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

export function addProjectHasError(hasError) {
    return {
        type: 'ADD_PROJECT_HAS_ERROR',
        hasError
    }
}

export function addProjectIsLoading(isLoading) {
    return {
        type: 'ADD_PROJECT_IS_LOADING',
        isLoading
    }
}

export function addProjectDataSuccess(data) {
    return {
        type: 'ADD_PROJECT_DATA_SUCCESS',
        data
    }
}

export const addProject = genericDispatch(
    addProjectHasError, addProjectIsLoading, addProjectDataSuccess, 'POST'
)

export function modifyProjectHasError(hasError) {
    return {
        type: 'MODIFY_PROJECT_HAS_ERROR',
        hasError
    }
}

export function modifyProjectIsLoading(isLoading) {
    return {
        type: 'MODIFY_PROJECT_IS_LOADING',
        isLoading
    }
}

export function modifyProjectDataSuccess(data) {
    return {
        type: 'MODIFY_PROJECT_DATA_SUCCESS',
        data
    }
}

export const modifyProject = genericDispatch(
    modifyProjectHasError, modifyProjectIsLoading, modifyProjectDataSuccess, 'POST'
)