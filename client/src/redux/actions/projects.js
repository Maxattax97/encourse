import {genericDispatch} from "./fetch"

const class_type = 'PROJECT'

export function setCurrentProject(id, index) {
    return {
        class: class_type,
        type: 'SET_CURRENT',
        id,
        index,
    }
}

export function toggleHidden() {
    return {
	    class: class_type,
        type: 'TOGGLE_HIDDEN',
    }
}

export const getClassProjects = genericDispatch(
    class_type, 'GET_ALL', 'GET'
)

export const addProject = genericDispatch(
    class_type, 'ADD', 'POST'
)

export const deleteProject = genericDispatch(
    class_type, 'DELETE', 'DELETE'
)

export const modifyProject = genericDispatch(
    class_type, 'MODIFY', 'POST'
)

export const addTest = genericDispatch(
    class_type, 'ADD_TEST', 'POST'
)

export const runTestSuite = genericDispatch(
    class_type, 'RUN_TEST_SUITE', 'POST'
)

export const getTestScripts = genericDispatch(
    class_type, 'GET_TEST_SCRIPTS', 'GET'
)

export const getTestSuites = genericDispatch(
    class_type, 'GET_TEST_SUITES', 'GET'
)

export const getOperation = genericDispatch(
    class_type, 'GET_OPERATION', 'GET'
)