function course(state = {}, action) {

    switch(action.type) {
        case 'GET_STUDENT_PREVIEWS_HAS_ERROR':
            return Object.assign({}, state, {
                getStudentPreviewsHasError: action.hasError,
            })
        case 'GET_STUDENT_PREVIEWS_IS_LOADING':
            return Object.assign({}, state, {
                getStudentPreviewsIsLoading: action.isLoading,
            })
        case 'GET_STUDENT_PREVIEWS_DATA_SUCCESS':
            return Object.assign({}, state, {
                getStudentPreviewsData: action.data,
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
            })
        default:
            return state
    }
}

export default course