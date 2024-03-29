import store from '../store'
import {getClassProjects, getTestScripts} from '../actions'
import {
    api_v1,
    courseID_v1,
    semester_v1,
    projectID_v1,
    studentID_v1,
    page_v1,
    size_v1,
    course_v2
} from './retrieval-utils'
import {getOperation, getSuiteGrades, getTestSuites} from '../actions/projects'

export function retrieveAllProjects(courseID) {
    store.dispatch(getClassProjects(`${course_v2}/projects`, {
        "Content-Type": "application/json"
    }, JSON.stringify(courseID)))
}

export function retrieveTestScripts(project) {
    store.dispatch(getTestScripts(`${api_v1}testScriptData?${projectID_v1(project)}&${page_v1(1)}&${size_v1(200)}`))
}

export function retrieveTestSuites(project) {
    store.dispatch(getTestSuites(`${api_v1}suites?${projectID_v1(project)}&${page_v1(1)}&${size_v1(200)}`))
}

export function retrieveSuitesScore(student, project) {
    store.dispatch(getSuiteGrades(`${api_v1}suitesData?${projectID_v1(project)}&${studentID_v1(student)}`))
}

export function retrieveOperation(project) {
    store.dispatch(getOperation(`${api_v1}operationData?${projectID_v1(project)}`))
}