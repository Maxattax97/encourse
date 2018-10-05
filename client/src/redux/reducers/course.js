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
        case 'GET_CLASS_PROGRESS_HAS_ERROR':
            return Object.assign({}, state, {
                getClassProgressHasError: action.hasError,
                getClassProgressIsFinished: true
            })
        case 'GET_CLASS_PROGRESS_IS_LOADING':
            return Object.assign({}, state, {
                getClassProgressIsLoading: action.isLoading,
                getClassProgressIsFinished: false
            })
        case 'GET_CLASS_PROGRESS_DATA_SUCCESS':
            return Object.assign({}, state, {
                getClassProgressData: action.data,
                getClassProgressIsFinished: true
            })
        case 'GET_TEST_BAR_GRAPH_HAS_ERROR':
            return Object.assign({}, state, {
                getTestBarGraphHasError: action.hasError,
                getTestBarGraphIsFinished: true
            })
        case 'GET_TEST_BAR_GRAPH_IS_LOADING':
            return Object.assign({}, state, {
                getTestBarGraphIsLoading: action.isLoading,
                getTestBarGraphIsFinished: false
            })
        case 'GET_TEST_BAR_GRAPH_DATA_SUCCESS':
            return Object.assign({}, state, {
                getTestBarGraphData: action.data,
                getTestBarGraphIsFinished: true
            })
        case 'SET_DIRECTORY_HAS_ERROR':
            return Object.assign({}, state, {
                setDirectoryHasError: action.hasError,
            })
        case 'SET_DIRECTORY_IS_LOADING':
            return Object.assign({}, state, {
                setDirectoryIsLoading: action.isLoading,
            })
        case 'SET_DIRECTORY_DATA_SUCCESS':
            return Object.assign({}, state, {
                setDirectoryData: action.data,
            })
        default:
            return state
    }
}

export default course