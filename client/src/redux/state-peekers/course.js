
import { defaultCourse, defaultSemester } from '../../defaults'

const createPeeker = (name) => (state) => state.course && state.course[name] ? state.course[name] : { loading: true, data: [] }

export const getStats = createPeeker('stats')

export function getStudents(state) {
	return state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : []
}

export const getCourseProgress = createPeeker('courseProgress')

export const getCourseTestProgress = createPeeker('courseTestProgress')

export const getStudentsProgress = createPeeker('studentsProgress')

export const getStudentsTestProgress = createPeeker('studentsTestProgress')

export function getCurrentCourseId(state) {
	return state.course && state.currentCourseId ? state.course.currentCourseId : defaultCourse
}

export function getCurrentSemesterId(state) {
	return state.course && state.currentSemesterId ? state.course.currentSemesterId : defaultSemester
}