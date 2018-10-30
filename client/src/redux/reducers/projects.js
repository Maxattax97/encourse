function projects(state = {}, action) {

    switch(action.type) {
    case 'SET_CURRENT_PROJECT':
        return Object.assign({}, state, {
            currentProjectId: action.id,
            currentProjectIndex: action.index,
        })
    case 'TOGGLE_HIDDEN':
        return Object.assign({}, state, {
            isHidden: !state.isHidden,
        })
    case 'GET_CLASS_PROJECTS':
        return Object.assign({}, state, {
            getClassProjectsIsLoading: true,
        })
    case 'GET_CLASS_PROJECTS_HAS_ERROR':
        return Object.assign({}, state, {
            getClassProjectsHasError: action.hasError,
            getClassProjectsIsLoading: false,
        })
    case 'GET_CLASS_PROJECTS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getClassProjectsData: action.data,
            currentProjectId: state.currentProjectId === null || state.currentProjectId === undefined ? 
                action.data[0].id : state.currentProjectId,
            currentProjectIndex: isNaN(state.currentProjectIndex) ? 0 : state.currentProjectIndex,
            getClassProjectsIsLoading: false,
        })
    case 'MODIFY_PROJECT':
        return Object.assign({}, state, {
            modifyProjectIsLoading: true,
        })
    case 'MODIFY_PROJECT_HAS_ERROR':
        return Object.assign({}, state, {
            modifyProjectHasError: action.hasError,
            modifyProjectIsLoading: false,
        })
    case 'MODIFY_PROJECT_DATA_SUCCESS': {
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
    case 'ADD_PROJECT':
        return Object.assign({}, state, {
            addProjectIsLoading: true,
        })
    case 'ADD_PROJECT_HAS_ERROR':
        return Object.assign({}, state, {
            addProjectHasError: action.hasError,
            addProjectIsLoading: false,
        })
    case 'ADD_PROJECT_DATA_SUCCESS': {
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
    case 'DELETE_PROJECT':
        return Object.assign({}, state, {
            deleteProjectIsLoading: true,
        })
    case 'DELETE_PROJECT_HAS_ERROR':
        return Object.assign({}, state, {
            deleteProjectHasError: action.hasError,
            deleteProjectIsLoading: false,
        })
    case 'DELETE_PROJECT_DATA_SUCCESS': {
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
    case 'ADD_TEST':
        return Object.assign({}, state, {
            addTestIsLoading: true,
        })
    case 'ADD_TEST_HAS_ERROR':
        return Object.assign({}, state, {
            addTestHasError: action.hasError,
            addTestIsLoading: false,
        })
    case 'ADD_TEST_DATA_SUCCESS':
        return Object.assign({}, state, {
            addTestData: action.data,
            addTestIsLoading: false,
        })
    default:
        return state
    }
}

export default projects