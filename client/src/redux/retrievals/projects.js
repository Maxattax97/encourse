import store from '../store'
import {getClassProjects, getTestScripts} from '../actions'
import {api_v1, courseID_v1, semester_v1, projectID_v1, studentID_v1} from './retrieval-utils'
import {getOperation, getTestSuites} from '../actions/projects'

export function retrieveAllProjects(courseID, semester) {
    store.dispatch(getClassProjects(`${api_v1}projectsData?${courseID_v1(courseID)}&${semester_v1(semester)}`))
}

export function retrieveTestScripts(project) {
    store.dispatch(getTestScripts(`${api_v1}testScriptData?${projectID_v1(project)}`))
}

export function retrieveTestSuites(student, project) {
    store.dispatch(getTestSuites(`${api_v1}suites?${projectID_v1(project)}&${studentID_v1(student)}`))
}

export function retrieveOperation(project) {
    store.dispatch(getOperation(`${api_v1}operationData?${projectID_v1(project)}`))
}