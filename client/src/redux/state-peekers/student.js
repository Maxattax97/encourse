export function getCurrentStudent(state) {
	return state.student && state.student.currentStudent ? state.student.currentStudent : null
}

export function getStudentStatistics(state) {
	return state.student && state.student.stats ? state.student.stats : {}
}