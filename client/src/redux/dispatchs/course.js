import store from "../store"
import url from '../../server'
import {getStudentPreviews} from "../actions"

export function getAllStudentsInfo() {
	const state = store.getState()
	const currentProjectId = state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
	store.dispatch(getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&size=215&projectID=${currentProjectId}`, null, null))
}