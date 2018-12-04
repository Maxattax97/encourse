const createPeeker = (name) => (state) => state.student && state.student[name] ? state.student[name] : { loading: true, data: [] }

export function getCurrentStudent(state) {
	return state.student && state.student.student ? state.student.student.data : null
}

export const getStudentStatistics = createPeeker('stats')

export const getStudentCommitFrequency = createPeeker('commitFrequency')

export const getStudentCodeChanges = createPeeker('codeChanges')

export const getStudentProgress = createPeeker('studentProgress')

export const getStudentCommitHistory = createPeeker('commitHistory')

export function isLastCommitsPage(state) {
	return state.student && state.student.commitHistory ? (state.student.commitHistory.data || {last: true}).last : true
}