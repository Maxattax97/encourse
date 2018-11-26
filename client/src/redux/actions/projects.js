import {genericDispatch} from "./index"

const type = 'PROJECT'

export function setCurrentProject(id, index) {
    return {
        type,
        class: 'SET_CURRENT',
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

export const getClassProjects = genericDispatch(
    type, 'GET_ALL', 'GET'
)

export const addProject = genericDispatch(
    type, 'ADD', 'POST'
)

export const deleteProject = genericDispatch(
    type, 'DELETE', 'DELETE'
)

export const modifyProject = genericDispatch(
    type, 'MODIFY', 'POST'
)

export const addTest = genericDispatch(
    type, 'ADD_TEST', 'POST'
)