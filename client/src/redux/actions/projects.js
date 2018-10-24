import genericDispatch from './fetch'

export function setCurrentProject(id, index) {
    return {
        type: 'SET_CURRENT_PROJECT',
        id,
        index,
    }
}

export function toggleHidden() {
    return {
        type: 'TOGGLE_HIDDEN',
    }
}

export function getClassProjectsHasError(hasError) {
    return {
        type: 'GET_CLASS_PROJECTS_HAS_ERROR',
        hasError
    }
}

export function getClassProjectsDataSuccess(data) {
    return {
        type: 'GET_CLASS_PROJECTS_DATA_SUCCESS',
        data
    }
}

export const getClassProjects = genericDispatch(
    'GET_CLASS_PROJECTS', getClassProjectsHasError, getClassProjectsDataSuccess, 'GET'
)

export function addProjectHasError(hasError) {
    return {
        type: 'ADD_PROJECT_HAS_ERROR',
        hasError
    }
}

export function addProjectDataSuccess(data) {
    return {
        type: 'ADD_PROJECT_DATA_SUCCESS',
        data
    }
}

export const addProject = genericDispatch(
    'ADD_PROJECT', addProjectHasError, addProjectDataSuccess, 'POST'
)

export function deleteProjectHasError(hasError) {
    return {
        type: 'DELETE_PROJECT_HAS_ERROR',
        hasError
    }
}

export function deleteProjectDataSuccess(data) {
    return {
        type: 'DELETE_PROJECT_DATA_SUCCESS',
        data
    }
}

export const deleteProject = genericDispatch(
    'DELETE_PROJECT', deleteProjectHasError, deleteProjectDataSuccess, 'DELETE'
)

export function modifyProjectHasError(hasError) {
    return {
        type: 'MODIFY_PROJECT_HAS_ERROR',
        hasError
    }
}

export function modifyProjectDataSuccess(data) {
    return {
        type: 'MODIFY_PROJECT_DATA_SUCCESS',
        data
    }
}

export const modifyProject = genericDispatch(
    'MODIFY_PROJECT', modifyProjectHasError, modifyProjectDataSuccess, 'POST'
)

export function addTestHasError(hasError) {
    return {
        type: 'ADD_TEST_HAS_ERROR',
        hasError
    }
}

export function addTestDataSuccess(data) {
    return {
        type: 'ADD_TEST_DATA_SUCCESS',
        data
    }
}

export const addTest = genericDispatch(
    'ADD_TEST', addTestHasError, addTestDataSuccess, 'POST'
)