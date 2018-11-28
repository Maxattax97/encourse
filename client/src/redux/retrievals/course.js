import store from "../store"

import {
	getClassProgress,
	getClassProgressAnon,
	getClassStatistics,
	getStudentPreviews, getTestBarGraph,
	getTestBarGraphAnon
} from "../actions"
import {api_v1, projectID_v1, semester_v1, courseID_v1} from "./retrieval-utils"

export function retrieveAllStudents(project, courseID, semester) {
	store.dispatch(getStudentPreviews(`${api_v1}studentsData?${courseID_v1(courseID)}&${semester_v1(semester)}&size=215&${projectID_v1(project)}`))
}

export function retrieveCourseStats(project) {
	store.dispatch(getClassStatistics(`${api_v1}classStatistics?${projectID_v1(project)}`))
}

export function retrieveCourseProgress(project) {
	store.dispatch(getClassProgressAnon(`${api_v1}progress?${projectID_v1(project)}&anonymous=true`))
}

export function retrieveCourseTestProgress(project) {
	store.dispatch(getTestBarGraphAnon(`${api_v1}testSummary?${projectID_v1(project)}`))
}

export function retrieveStudentsProgress(project) {
	store.dispatch(getClassProgress(`${api_v1}progress?${projectID_v1(project)}`))
}

export function retrieveStudentsTestProgress(project) {
	store.dispatch(getTestBarGraph(`${api_v1}testSummary?${projectID_v1(project)}`))
}