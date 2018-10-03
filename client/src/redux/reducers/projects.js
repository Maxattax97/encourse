function projects(state = {}, action) {

    switch(action.type) {
        case 'SET_CURRENT_PROJECT':
            return Object.assign({}, state, {
                currentProjectId: action.id,
                currentProjectIndex: action.index,
            })
        case 'GET_CLASS_PROJECTS_HAS_ERROR':
            return Object.assign({}, state, {
                getClassProjectsHasError: action.hasError,
            })
        case 'GET_CLASS_PROJECTS_IS_LOADING':
            return Object.assign({}, state, {
                getClassProjectsIsLoading: action.isLoading,
            })
        case 'GET_CLASS_PROJECTS_DATA_SUCCESS':
            return Object.assign({}, state, {
                getClassProjectsData: action.data,
                currentProjectId: action.data[0].id
            })
        default:
            return state
    }
}

export default projects