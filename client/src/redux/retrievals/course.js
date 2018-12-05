import store from "../store"

import {
    getClassProgress,
    getClassProgressAnon,
    getClassStatistics, getSectionsData, getSimilarityPlot,
    getStudentPreviews, getTestBarGraph,
    getTestBarGraphAnon
} from '../actions'
import {api_v1, projectID_v1, semester_v1, courseID_v1, size_v1, anon_v1} from './retrieval-utils'
import {getTeachingAssistants} from '../actions/course'

export function retrieveAllStudents(project, courseID, semester) {
	store.dispatch(getStudentPreviews(`${api_v1}studentsData?${courseID_v1(courseID)}&${semester_v1(semester)}&${size_v1(215)}&${projectID_v1(project)}`))
}

export function retrieveCourseStats(project) {
	store.dispatch(getClassStatistics(`${api_v1}classStatistics?${projectID_v1(project)}`))
}

export function retrieveCourseProgress(project) {
	store.dispatch(getClassProgressAnon(`${api_v1}progress?${projectID_v1(project)}&${anon_v1}`))
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

export function retrieveStudentsSimilarity(project) {
	store.dispatch(getSimilarityPlot(`${api_v1}classSimilar?${projectID_v1(project)}`))
}

export function retrieveAllTeachingAssistants(courseID, semester) {
    store.dispatch(getTeachingAssistants(`${api_v1}teachingAssistantsData?${courseID_v1(courseID)}&${semester_v1(semester)}`))
}

export function retrieveAllSections(courseID, semester) {
    store.dispatch(getSectionsData(`${api_v1}sectionsData?${courseID_v1(courseID)}&${semester_v1(semester)}`))
}