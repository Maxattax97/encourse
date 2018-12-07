const createPeeker = (name) => (state) => state.projects && state.projects[name] ? state.projects[name] : { loading: true, data: [] }

export function getProjects(state) {
	return state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : []
}

export function getCurrentProjectIndex(state) {
	return state.projects && state.projects.currentProjectIndex ? state.projects.currentProjectIndex : 0
}

export function getCurrentProject(state) {
	const projects = getProjects(state)
	return projects.length ? projects[getCurrentProjectIndex(state)] : null
}

export const getTestScripts = createPeeker('getTestScripts')

export const getTestSuites = createPeeker('getTestSuites')

export const getOperation = createPeeker('getOperation')