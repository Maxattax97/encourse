function course(state = {}, action) {
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
    case 'GET_SECTIONS_DATA':
        return Object.assign({}, state, {
            getSectionsDataIsLoading: true
        })
    case 'GET_SECTIONS_DATA_HAS_ERROR':
        return Object.assign({}, state, {
            getSectionsDataHasError: action.hasError,
            getSectionsDataIsLoading: false
        })
    case 'GET_SECTIONS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getSectionsData: action.data,
            getSectionsDataIsLoading: false
        })
    case 'GET_CLASS_PROGRESS':
        return Object.assign({}, state, {
            getClassProgressIsLoading: true,
        })
    case 'GET_CLASS_PROGRESS_HAS_ERROR':
        return Object.assign({}, state, {
            getClassProgressHasError: action.hasError,
            getClassProgressIsLoading: false,
        })
    case 'GET_CLASS_PROGRESS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getClassProgressData: action.data,
            getClassProgressIsLoading: false
        })
    case 'GET_TEST_BAR_GRAPH':
        return Object.assign({}, state, {
            getTestBarGraphIsLoading: true,
        })
    case 'GET_TEST_BAR_GRAPH_HAS_ERROR':
        return Object.assign({}, state, {
            getTestBarGraphHasError: action.hasError,
            getTestBarGraphIsLoading: false,
        })
    case 'GET_TEST_BAR_GRAPH_DATA_SUCCESS':
        return Object.assign({}, state, {
            getTestBarGraphData: action.data,
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
	case 'SYNC_REPOSITORIES':
		return Object.assign({}, state, {
			syncRepositoriesIsLoading: true
		})
        case 'SYNC_REPOSITORIES_HAS_ERROR':
            return Object.assign({}, state, {
                syncRepositoriesIsLoading: false,
                syncRepositoriesHasError: action.hasError
            })
        case 'SYNC_REPOSITORIES_SUCCESS':
            return Object.assign({}, state, {
                syncRepositoriesData: action.data,
                syncRepositoriesIsLoading: false
            })
	    case 'RUN_TESTS':
		    return Object.assign({}, state, {
			    runTestsIsLoading: true
		    })
	    case 'RUN_TESTS_HAS_ERROR':
		    return Object.assign({}, state, {
			    runTestsIsLoading: false,
			    runTestsHasError: action.hasError
		    })
	    case 'RUN_TESTS_SUCCESS':
		    return Object.assign({}, state, {
			    runTestsData: action.data,
			    runTestsIsLoading: false
		    })
    default:
        return state
    }
}

export default course