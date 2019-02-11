import store from "../store"

import {
    getClassProgress,
    getClassProgressAnon,
    getClassStatistics, getDishonestyReport, getSectionsData, getSimilarityPlot,
    getStudentPreviews, getTestBarGraph,
    getTestBarGraphAnon
} from '../actions'
import {
    api_v1,
    projectID_v1,
    semester_v1,
    courseID_v1,
    size_v1,
    anon_v1,
    userList_v1,
    page_v1,
    course_v2
} from './retrieval-utils'
import {getCourse, getCourseCharts, getCourseFilterCharts, getTeachingAssistants} from '../actions/course'

export function retrieveCourse(courseID) {
    store.dispatch(getCourse(`${course_v2}/get`, {
        "Content-Type": "application/json"
    }, JSON.stringify(courseID)))
}

export function retrieveAllStudents(project) {
	store.dispatch(getStudentPreviews(`${course_v2}/students`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
	    "projectID": project.projectID,
        "options": {
            "student": true,
            "projectInfo": true
        }
    })))
}

export function retrieveCourseCharts(project) {
    store.dispatch(getCourseCharts(`${course_v2}/project/date`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "options": project.runTestall ? {
            "progressChart": true,
            "commitChart": true,
            "timeChart": true
        } : {
            "commitChart": true,
            "timeChart": true
        }
    })))
}

export function retrieveCourseDishonestyCharts(project) {
    store.dispatch(getCourseCharts(`${course_v2}/project/date`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "options": {
            "changesChart": true,
            "similarityChart": true,
            "timeVelocityChart": true,
            "commitVelocityChart": true
        }
    })))
}

export function retrieveCourseFilterCharts(project, commits, time, progress, view, students, selectedAll) {
    store.dispatch(getCourseFilterCharts(`${course_v2}/project/date`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "options": project.runTestall ? {
            "progressChart": true,
            "commitChart": true,
            "timeChart": true
        } : {
            "commitChart": true,
            "timeChart": true
        },
        "filters" : {
            "commits": commits,
            "time": time,
            "progress": progress,
            "view": view,
            "students": students,
            "selectedAll": selectedAll
        }
    })))
}

export function retrieveCourseDishonestyFilterCharts(project, commits, time, progress, view, students, selectedAll) {
    store.dispatch(getCourseFilterCharts(`${course_v2}/project/date`, {
        "Content-Type": "application/json"
    }, JSON.stringify({
        "projectID": project.projectID,
        "options": {
            "changesChart": true,
            "similarityChart": true,
            "timeVelocityChart": true,
            "commitVelocityChart": true
        },
        "filters" : {
            "commits": commits,
            "time": time,
            "progress": progress,
            "view": view,
            "students": students,
            "selectedAll": selectedAll
        }
    })))
}

export function retrieveCourseStats(project) {
	store.dispatch(getClassStatistics(`${api_v1}classStatistics?${projectID_v1(project)}`))
}

export function retrieveCourseProgress(project) {
	store.dispatch(getClassProgressAnon(`${api_v1}progress?${projectID_v1(project)}&${anon_v1}`))
}

export function retrieveCourseTestProgress(project) {
	store.dispatch(getTestBarGraphAnon(`${api_v1}testSummary?${projectID_v1(project)}&${anon_v1}`))
}

export function retrieveStudentsProgress(project) {
	store.dispatch(getClassProgress(`${api_v1}progress?${projectID_v1(project)}`))
}

export function retrieveStudentsProgressSpecific(project, usernames) {
	store.dispatch(getClassProgress(`${api_v1}progress?${projectID_v1(project)}&${userList_v1(usernames)}`))
}

export function retrieveStudentsTestProgress(project) {
	store.dispatch(getTestBarGraph(`${api_v1}testSummary?${projectID_v1(project)}`))
}

export function retrieveStudentsTestProgressSpecific(project, usernames) {
	store.dispatch(getTestBarGraph(`${api_v1}testSummary?${projectID_v1(project)}&${userList_v1(usernames)}`))
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

export function retrieveDishonestyReport(project, page = 1, size = 200) {
    store.dispatch(getDishonestyReport(`${api_v1}classCheating?${projectID_v1(project)}&${page_v1(page)}&${size_v1(size)}`))
}