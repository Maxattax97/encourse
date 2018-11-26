import {genericDispatch} from "./index"

const type = 'PROJECT'

export function setCurrentProject(id, index) {
    return {
        type,
        class: 'SET_CURRENT_PROJECT',
        id,
        index,
    }
}

export function toggleHidden() {
    return {
	    type,
	    class: 'TOGGLE_HIDDEN',
    }
}

export function getClassProjectsHasError(hasError) {
    return {
	    type,
	    class: 'GET_CLASS_PROJECTS_HAS_ERROR',
        hasError
    }
}

export function getClassProjectsDataSuccess(data) {
    return {
	    type,
	    class: 'GET_CLASS_PROJECTS_DATA_SUCCESS',
        data
    }
}

export const getClassProjects = genericDispatch(
    type, 'GET_CLASS_PROJECTS', 'GET'
)

export const addProject = genericDispatch(
    type, 'ADD_PROJECT', 'POST'
)

export const deleteProject = genericDispatch(
    type, 'DELETE_PROJECT', 'DELETE'
)

export const modifyProject = genericDispatch(
    type, 'MODIFY_PROJECT', 'POST'
)

export const addTest = genericDispatch(
    type, 'ADD_TEST', 'POST'
)