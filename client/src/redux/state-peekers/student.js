const createPeeker = (name) => (state) => state.student && state.student[name] ? state.student[name] : { loading: true, data: [] }

export function getCurrentStudent(state) {
	return state.student && state.student.student ? state.student.student.data : null
}

export function getCurrentCommit(state) {
	console.log(state.student)
	return state.student && state.student.currentCommit ? state.student.currentCommit.data : null
}

export const getStudentStatistics = createPeeker('stats')

export const getStudentCharts = createPeeker('studentCharts')

export const getStudentComparisons = createPeeker('studentComparisons')

export const getStudentCommitFrequency = createPeeker('commitFrequency')

export const getStudentCodeChanges = createPeeker('codeChanges')

export const getStudentProgress = createPeeker('studentProgress')

export const getStudentCommitHistory = createPeeker('commitHistory')

export const getCommitSource = createPeeker('source')

export function isLastCommitsPage(state) {
	return state.student && state.student.commitHistory ? (state.student.commitHistory.data || {last: true}).last : true
}