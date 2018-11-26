import store from "../store"
import url from '../../server'
import {
	getClassProgress,
	getClassProgressAnon,
	getClassStatistics,
	getStudentPreviews, getTestBarGraph,
	getTestBarGraphAnon
} from "../actions"

export function retrieveAllStudents(project) {
	store.dispatch(getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&size=215&projectID=${project.id}`))
}

export function retrieveStats(project) {
	store.dispatch(getClassStatistics(`${url}/api/classStatistics?projectID=${project.id}`))
}

export function retrieveCourseProgress(project) {
	store.dispatch(getClassProgressAnon(`${url}/api/progress?projectID=${project.id}&anonymous=true`))
}

export function retrieveCourseTestProgress(project) {
	store.dispatch(getTestBarGraphAnon(`${url}/api/testSummary?projectID=${project.id}`))
}

export function retrieveStudentsProgress(project) {
	store.dispatch(getClassProgress(`${url}/api/progress?projectID=${project.id}`))
}

export function retrieveStudentsTestProgress(project) {
	store.dispatch(getTestBarGraph(`${url}/api/testSummary?projectID=${project.id}`))
}