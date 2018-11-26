export function getProjects(state) {
	return state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : []
}

export function getCurrentProjectIndex(state) {
	return state.projects && state.currentProjectIndex ? state.projects.currentProjectIndex : 0
}

export function getCurrentProject(state) {
	const projects = getProjects(state)
	return projects.length ? projects[getCurrentProjectIndex(state)] : null
}