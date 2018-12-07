
import { defaultCourse, defaultSemester } from '../../defaults'

const createPeeker = (name) => (state) => state.course && state.course[name] ? state.course[name] : { loading: true, data: [] }

export const getCourseStats = createPeeker('stats')

export const getStudentsStats = createPeeker('studentsStats')

export const getStudents = createPeeker('students')

export const getSections = createPeeker('sections')

export const getCourseProgress = createPeeker('courseProgress')

export const getCourseTestProgress = createPeeker('courseTestProgress')

export const getStudentsProgress = createPeeker('studentsProgress')

export const getStudentsTestProgress = createPeeker('studentsTestProgress')

export const getStudentsSimilarity = createPeeker('studentsSimilarity')

export const getTeachingAssistants = createPeeker('teachingAssistants')

export const getDishonestyReport = createPeeker('dishonestyReport')

export function getCurrentCourseId(state) {
	return state.course && state.course.currentCourseId ? state.course.currentCourseId : defaultCourse
}

export function getCurrentSemesterId(state) {
	return state.course && state.course.currentSemesterId ? state.course.currentSemesterId : defaultSemester
}

export function getCurrentTAIndex(state) {
    return state.course && state.course.currentTAIndex ? state.course.currentTAIndex : 0
}

export function getCurrentTA(state) {
    const tas = getTeachingAssistants(state)
    return tas.data.length ? state.course.currentTA ? state.course.currentTA : tas.data[getCurrentTAIndex(state)] : null
}