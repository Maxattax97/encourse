function course(state = {}, action) {
    //TODO: get rid of isFinished
    switch(action.type) {
    case 'GET_STUDENT_PREVIEWS':
        return Object.assign({}, state, {
            getStudentPreviewsIsLoading: true,
        })
    case 'GET_STUDENT_PREVIEWS_HAS_ERROR':
        return Object.assign({}, state, {
            getStudentPreviewsHasError: action.hasError,
            getStudentPreviewsIsLoading: false,
        })
    case 'GET_STUDENT_PREVIEWS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getStudentPreviewsData: action.data,
            getStudentPreviewsIsLoading: false,
        })
    case 'GET_CLASS_PROGRESS':
        return Object.assign({}, state, {
            getClassProgressIsLoading: true,
            getClassProgressIsFinished: false,
        })
    case 'GET_CLASS_PROGRESS_HAS_ERROR':
        return Object.assign({}, state, {
            getClassProgressHasError: action.hasError,
            getClassProgressIsFinished: true,
            getClassProgressIsLoading: false,
        })
    case 'GET_CLASS_PROGRESS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getClassProgressData: action.data,
            getClassProgressIsFinished: true,
            getClassProgressIsLoading: false
        })
    case 'GET_TEST_BAR_GRAPH':
        return Object.assign({}, state, {
            getTestBarGraphIsLoading: true,
            getTestBarGraphIsFinished: false
        })
    case 'GET_TEST_BAR_GRAPH_HAS_ERROR':
        return Object.assign({}, state, {
            getTestBarGraphHasError: action.hasError,
            getTestBarGraphIsFinished: true,
            getTestBarGraphIsLoading: false,
        })
    case 'GET_TEST_BAR_GRAPH_DATA_SUCCESS':
        return Object.assign({}, state, {
            getTestBarGraphData: action.data,
            getTestBarGraphIsFinished: true,
            getTestBarGraphIsLoading: false,
        })
    case 'SET_DIRECTORY':
        return Object.assign({}, state, {
            setDirectoryIsLoading: true,
        })
    case 'SET_DIRECTORY_HAS_ERROR':
        return Object.assign({}, state, {
            setDirectoryHasError: action.hasError,
            setDirectoryIsLoading: false,
        })
    case 'SET_DIRECTORY_DATA_SUCCESS':
        return Object.assign({}, state, {
            setDirectoryData: action.data,
            setDirectoryIsLoading: false,
        })
    default:
        return state
    }
}

export default course