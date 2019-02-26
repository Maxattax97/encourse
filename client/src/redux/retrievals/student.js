import store from "../store"
import {
	getCodeFrequency,
	getCommitFrequency,
	getCommitHistory,
	getProgressLine,
	getStatistics,
	getStudent,
	getSource
} from "../actions"
import {
    api_v1,
    page_v1,
    projectID_v1,
    size_v1,
    studentID_v1,
    courseID_v1,
    semester_v1,
    student_v2
} from './retrieval-utils'
import {getStudentCharts, getStudentComparisons} from '../actions/student'

export function retrieveStudent(studentID) {
	store.dispatch(getStudent(`${student_v2}`, {
        "Content-Type": "application/json"
    }, JSON.stringify(studentID)))
}

export function retrieveStudentCharts(project, student) {
    store.dispatch(getStudentCharts(`${student_v2}/project/diffs`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "studentID": student.studentID
    })))
}

export function retrieveStudentComparisons(project, student) {
    store.dispatch(getStudentComparisons(`${student_v2}/project/comparison`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "studentID": student.studentID
    })))
}

export function retrieveStudentStats(student, project) {
	store.dispatch(getStatistics(`${api_v1}statistics?${projectID_v1(project)}&${studentID_v1(student)}`))
}

export function retrieveStudentCommitFrequency(student, project) {
	store.dispatch(getCommitFrequency(`${api_v1}commitCount?${projectID_v1(project)}&${studentID_v1(student)}`))
}

export function retrieveStudentCodeChanges(student, project) {
	store.dispatch(getCodeFrequency(`${api_v1}diffs?${projectID_v1(project)}&${studentID_v1(student)}`))
}

export function retrieveStudentProgress(student, project) {
	store.dispatch(getProgressLine(`${api_v1}progress?${projectID_v1(project)}&${studentID_v1(student)}`))
}

export function retrieveStudentCommitHistory(student, project, page, size) {
	store.dispatch(getCommitHistory(`${api_v1}commitList?${projectID_v1(project)}&${studentID_v1(student)}&${page_v1(page)}&${size_v1(size)}`))
}

export function retrieveSource(project, student, startCommit) {
    store.dispatch(getSource(`${student_v2}/project/commit`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "studentID": student.studentID,
        "commit": startCommit.hash
    })))
}