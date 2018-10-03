function student(state = {}, action) {

    switch(action.type) {
        case 'SET_CURRENT_STUDENT':
            return Object.assign({}, state, {
                currentStudent: action.student,
            })
        case 'CLEAR_STUDENT':
            return Object.assign({}, state, {
                currentStudent: null,
                getProgressLineData: null,
                getCommitFrequencyData: null,
                getCodeFrequencyData: null,
                getStatistics: null,
            })
        case 'GET_PROGRESS_LINE_HAS_ERROR':
            return Object.assign({}, state, {
                getProgressLineHasError: action.hasError,
            })
        case 'GET_PROGRESS_LINE_IS_LOADING':
            return Object.assign({}, state, {
                getProgressLineIsLoading: action.isLoading,
            })
        case 'GET_PROGRESS_LINE_DATA_SUCCESS':
            return Object.assign({}, state, {
                getProgressLineData: action.data,
            })
        case 'GET_COMMIT_FREQUENCY_HAS_ERROR':
            return Object.assign({}, state, {
                getCommitFrequencyHasError: action.hasError,
            })
        case 'GET_COMMIT_FREQUENCY_IS_LOADING':
            return Object.assign({}, state, {
                getCommitFrequencyIsLoading: action.isLoading,
            })
        case 'GET_COMMIT_FREQUENCY_DATA_SUCCESS':
            return Object.assign({}, state, {
                getCommitFrequencyData: action.data,
            })
        case 'GET_CODE_FREQUENCY_HAS_ERROR':
            return Object.assign({}, state, {
                getCodeFrequencyHasError: action.hasError,
            })
        case 'GET_CODE_FREQUENCY_IS_LOADING':
            return Object.assign({}, state, {
                getCodeFrequencyIsLoading: action.isLoading,
            })
        case 'GET_CODE_FREQUENCY_DATA_SUCCESS':
            return Object.assign({}, state, {
                getCodeFrequencyData: action.data,
            })
        case 'GET_STATISTICS_HAS_ERROR':
            return Object.assign({}, state, {
                getStatisticsHasError: action.hasError,
            })
        case 'GET_STATISTICS_IS_LOADING':
            return Object.assign({}, state, {
                getStatisticsIsLoading: action.isLoading,
            })
        case 'GET_STATISTICS_DATA_SUCCESS':
            return Object.assign({}, state, {
                getStatisticsData: action.data,
            })
        default:
            return state
    }
}

export default student