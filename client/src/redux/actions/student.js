import {genericDispatch} from "./index"

const type = 'STUDENT'

export function updateCommitsPage() {
	return {
		type,
		class: 'UPDATE_COMMITS_PAGE'
	}
}

export function resetCommitsPage() {
	return {
		type,
		class: 'RESET_COMMITS_PAGE'
	}
}

export function setCurrentStudent(student) {
    return {
        type,
        class: 'SET_CURRENT',
        student
    }
}

export function clearStudent() {
    return {
	    type,
	    class: 'CLEAR'
    }
}

export const getStudent = genericDispatch(
    type, 'GET', 'GET'
)

export const getProgressLine = genericDispatch(
    type, 'GET_PROGRESS_LINE', 'GET'
)

export const getCodeFrequency = genericDispatch(
    type, 'GET_CODE_FREQUENCY',  'GET'
)

export const getCommitFrequency = genericDispatch(
    type, 'GET_COMMIT_FREQUENCY', 'GET'
)

export const getStatistics = genericDispatch(
    type, 'GET_STATISTICS', 'GET'
)

export const getCommitHistory = genericDispatch(
    type, 'GET_COMMIT_HISTORY', 'GET'
)

export const getProgressPerTime = genericDispatch(
    type, 'GET_PROGRESS_PER_TIME', 'GET'
)

export const getProgressPerCommit = genericDispatch(
    type, 'GET_PROGRESS_PER_COMMIT', 'GET'
)

export const syncStudentRepository = genericDispatch(
	type, 'SYNC', 'POST'
)

export const runStudentTests = genericDispatch(
    type, 'RUN_TESTS', 'POST'
)