import {genericDispatch} from "./fetch"

const class_type = 'STUDENT'

export function updateCommitsPage() {
	return {
		class: class_type,
		type: 'UPDATE_COMMITS_PAGE'
	}
}

export function resetCommitsPage() {
	return {
		class: class_type,
        type: 'RESET_COMMITS_PAGE'
	}
}

export function setCurrentStudent(student) {
    return {
        class: class_type,
        type: 'SET_CURRENT',
        student
    }
}

export function clearStudent() {
    return {
	    class: class_type,
        type: 'CLEAR'
    }
}

export const getStudent = genericDispatch(
    class_type, 'GET', 'POST'
)

export const getProgressLine = genericDispatch(
    class_type, 'GET_PROGRESS_LINE', 'GET'
)

export const getCodeFrequency = genericDispatch(
    class_type, 'GET_CODE_FREQUENCY',  'GET'
)

export const getCommitFrequency = genericDispatch(
    class_type, 'GET_COMMIT_FREQUENCY', 'GET'
)

export const getStatistics = genericDispatch(
    class_type, 'GET_STATISTICS', 'GET'
)

export const getCommitHistory = genericDispatch(
    class_type, 'GET_COMMIT_HISTORY', 'GET'
)

export const getProgressPerTime = genericDispatch(
    class_type, 'GET_PROGRESS_PER_TIME', 'GET'
)

export const getProgressPerCommit = genericDispatch(
    class_type, 'GET_PROGRESS_PER_COMMIT', 'GET'
)

export const syncStudentRepository = genericDispatch(
	class_type, 'SYNC', 'POST'
)

export const runStudentTests = genericDispatch(
    class_type, 'RUN_TESTS', 'POST'
)

export const getSource = genericDispatch(
    class_type, 'GET_SOURCE', 'GET'
)