import {forwardData, getData, unknownAction} from './reducer-utils'

function setCurrentProject(state, action) {
	return Object.assign({}, state, {
		currentProjectId: action.id,
		currentProjectIndex: action.index,
	})
}

function toggleHidden(state, action) {
	return Object.assign({}, state, {
		isHidden: !state.isHidden,
	})
}

function getClassProjects(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    getClassProjectsHasError: action.hasError,
		    getClassProjectsIsLoading: false,
	    })
    if(action.data)
	    return Object.assign({}, state, {
		    getClassProjectsData: action.data,
		    currentProjectId: state.currentProjectId === null || state.currentProjectId === undefined ?
			    action.data[0].id : state.currentProjectId,
		    currentProjectIndex: isNaN(state.currentProjectIndex) ? 0 : state.currentProjectIndex,
		    getClassProjectsIsLoading: false,
	    })
	return Object.assign({}, state, {
		getClassProjectsIsLoading: true,
	})
}

function modifyProject(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    modifyProjectHasError: action.hasError,
		    modifyProjectIsLoading: false,
	    })
    if(action.data) {
	    let projects3 = [...state.getClassProjectsData]
	    projects3[state.currentProjectIndex] = {
		    project_name: action.data.projectName,
		    source_name: action.data.repoName,
		    start_date: action.data.startDate,
		    due_date: action.data.dueDate,
		    id: action.data.projectIdentifier,
		    hidden_test_script: [],
		    test_script: [],
	    }
	    return Object.assign({}, state, {
		    modifyProjectData: action.data,
		    getClassProjectsData: projects3,
		    modifyProjectIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		modifyProjectIsLoading: true,
	})
}

function addProject(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    addProjectHasError: action.hasError,
		    addProjectIsLoading: false,
	    })
    if(action.data) {
	    let projects1 = [...state.getClassProjectsData]
	    projects1.push({
		    project_name: action.data.projectName,
		    source_name: action.data.repoName,
		    start_date: action.data.startDate,
		    due_date: action.data.dueDate,
		    id: action.data.projectIdentifier,
		    hidden_test_script: [],
		    test_script: [],
	    })
	    return Object.assign({}, state, {
		    addProjectData: action.data,
		    getClassProjectsData: projects1,
		    currentProjectIndex: projects1.length - 1,
		    currentProjectId: action.data.projectIdentifier,
		    addProjectIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		addProjectIsLoading: true,
	})
}

function deleteProject(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    deleteProjectHasError: action.hasError,
		    deleteProjectIsLoading: false,
	    })
    if(action.data) {
	    let projects2 = [...state.getClassProjectsData]
	    projects2.splice(state.currentProjectIndex, 1)
	    let currentProjectIndex = state.currentProjectIndex === 0 ?
		    (state.currentProjectIndex ? state.currentProjectIndex - 1 : null)
		    : 0
	    return Object.assign({}, state, {
		    deleteProjectData: action.data,
		    getClassProjectsData: projects2,
		    currentProjectIndex: currentProjectIndex,
		    currentProjectId: state.getClassProjectsData[currentProjectIndex].id,
		    deleteProjectIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		deleteProjectIsLoading: true,
	})
}

function formatTestSuites(udata) {
    return udata.sort((a, b) => a.stat_name.localeCompare(b.stat_name))
}

function formatTestScripts(udata) {
	return udata
}

export default function projects(state = {}, action) {
    if(action.class !== 'PROJECT')
        return state

    switch(action.type) {
        case 'SET_CURRENT':
            return setCurrentProject(state, action)
        case 'TOGGLE_HIDDEN':
            return toggleHidden(state, action)
        case 'GET_ALL':
            return getClassProjects(state, action)
        case 'MODIFY':
            return modifyProject(state, action)
        case 'ADD':
            return addProject(state, action)
        case 'DELETE':
            return deleteProject(state, action)
        case 'ADD_TEST':
            return getData(state, action, 'addTest')
        case 'RUN_TEST_SUITE':
            return getData(state, action, 'runTestSuite')
        case 'GET_TEST_SCRIPTS':
            return forwardData(state, action, 'getTestScripts', formatTestScripts, true)
        case 'GET_TEST_SUITES':
            return forwardData(state, action, 'getTestSuites', formatTestSuites)
        case 'GET_OPERATION':
            return forwardData(state, action, 'getOperation')
        default:
            return unknownAction(state, action)
    }
}