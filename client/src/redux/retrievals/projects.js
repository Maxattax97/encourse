import store from '../store'
import {getClassProjects} from '../actions'
import {api_v1, courseID_v1, semester_v1} from './retrieval-utils'

export function retrieveAllProjects(courseID, semester) {
    store.dispatch(getClassProjects(`${api_v1}projectsData?${courseID_v1(courseID)}&${semester_v1(semester)}`))
}