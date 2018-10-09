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
            getStatisticsData: null,
            getCommitHistoryData: null,
        })
    case 'GET_PROGRESS_LINE_HAS_ERROR':
        return Object.assign({}, state, {
            getProgressLineHasError: action.hasError,
            getProgressLineIsFinished: true
        })
    case 'GET_PROGRESS_LINE_IS_LOADING':
        return Object.assign({}, state, {
            getProgressLineIsLoading: action.isLoading,
            getProgressLineIsFinished: false
        })
    case 'GET_PROGRESS_LINE_DATA_SUCCESS':
        return Object.assign({}, state, {
            getProgressLineData: action.data,
            getProgressLineIsFinished: true
        })
    case 'GET_COMMIT_FREQUENCY_HAS_ERROR':
        return Object.assign({}, state, {
            getCommitFrequencyHasError: action.hasError,
            getCommitFrequencyIsFinished: true
        })
    case 'GET_COMMIT_FREQUENCY_IS_LOADING':
        return Object.assign({}, state, {
            getCommitFrequencyIsLoading: action.isLoading,
            getCommitFrequencyIsFinished: false
        })
    case 'GET_COMMIT_FREQUENCY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCommitFrequencyData: action.data,
            getCommitFrequencyIsFinished: true
        })
    case 'GET_CODE_FREQUENCY_HAS_ERROR':
        return Object.assign({}, state, {
            getCodeFrequencyHasError: action.hasError,
            getCodeFrequencyIsFinished: true
        })
    case 'GET_CODE_FREQUENCY_IS_LOADING':
        return Object.assign({}, state, {
            getCodeFrequencyIsLoading: action.isLoading,
            getCodeFrequencyIsFinished: false
        })
    case 'GET_CODE_FREQUENCY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCodeFrequencyData: action.data,
            getCodeFrequencyIsFinished: true
        })
    case 'GET_STATISTICS_HAS_ERROR':
        return Object.assign({}, state, {
            getStatisticsHasError: action.hasError,
            getStatisticsIsFinished: true
        })
    case 'GET_STATISTICS_IS_LOADING':
        return Object.assign({}, state, {
            getStatisticsIsLoading: action.isLoading,
            getStatisticsIsFinished: false
        })
    case 'GET_STATISTICS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getStatisticsData: action.data,
            getStatisticsIsFinished: true
        })
    case 'GET_COMMIT_HISTORY_HAS_ERROR':
        return Object.assign({}, state, {
            getCommitHistoryHasError: action.hasError,
            getCommitHistoryIsFinished: true
        })
    case 'GET_COMMIT_HISTORY_IS_LOADING':
        return Object.assign({}, state, {
            getCommitHistoryIsLoading: action.isLoading,
            getCommitHistoryIsFinished: false
        })
    case 'GET_COMMIT_HISTORY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCommitHistoryData: action.data,
            getCommitHistoryIsFinished: true
        })
    default:
        return state
    }
}

export default student