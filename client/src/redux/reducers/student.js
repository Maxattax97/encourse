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
    case 'GET_PROGRESS_LINE':
        return Object.assign({}, state, {
            getProgressLineIsLoading: true,
            getProgressLineIsFinished: false
        })
    case 'GET_PROGRESS_LINE_HAS_ERROR':
        return Object.assign({}, state, {
            getProgressLineHasError: action.hasError,
            getProgressLineIsFinished: true,
            getProgressLineIsLoading: false,
        })
    case 'GET_PROGRESS_LINE_DATA_SUCCESS':
        return Object.assign({}, state, {
            getProgressLineData: action.data,
            getProgressLineIsFinished: true,
            getProgressLineIsLoading: false,
        })
    case 'GET_COMMIT_FREQUENCY':
        return Object.assign({}, state, {
            getCommitFrequencyIsLoading: true,
            getCommitFrequencyIsFinished: false,
        })
    case 'GET_COMMIT_FREQUENCY_HAS_ERROR':
        return Object.assign({}, state, {
            getCommitFrequencyHasError: action.hasError,
            getCommitFrequencyIsFinished: true,
            getCommitFrequencyIsLoading: false,
        })
    case 'GET_COMMIT_FREQUENCY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCommitFrequencyData: action.data,
            getCommitFrequencyIsFinished: true,
            getCommitFrequencyIsLoading: false,
        })
    case 'GET_CODE_FREQUENCY':
        return Object.assign({}, state, {
            getCodeFrequencyIsLoading: true,
            getCodeFrequencyIsFinished: false,
        })
    case 'GET_CODE_FREQUENCY_HAS_ERROR':
        return Object.assign({}, state, {
            getCodeFrequencyHasError: action.hasError,
            getCodeFrequencyIsFinished: true,
            getCodeFrequencyIsLoading: false,
        })
    case 'GET_CODE_FREQUENCY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCodeFrequencyData: action.data,
            getCodeFrequencyIsFinished: true,
            getCodeFrequencyIsLoading: false,
        })
    case 'GET_STATISTICS':
        return Object.assign({}, state, {
            getStatisticsIsLoading: true,
            getStatisticsIsFinished: false,
        })
    case 'GET_STATISTICS_HAS_ERROR':
        return Object.assign({}, state, {
            getStatisticsHasError: action.hasError,
            getStatisticsIsFinished: true,
            getStatisticsIsLoading: false,
        })
    case 'GET_STATISTICS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getStatisticsData: action.data,
            getStatisticsIsFinished: true,
            getStatisticsIsLoading: false,
        })
    case 'GET_COMMIT_HISTORY':
        return Object.assign({}, state, {
            getCommitHistoryIsLoading: true,
            getCommitHistoryIsFinished: false,
        })
    case 'GET_COMMIT_HISTORY_HAS_ERROR':
        return Object.assign({}, state, {
            getCommitHistoryHasError: action.hasError,
            getCommitHistoryIsFinished: true,
            getCommitHistoryIsLoading: false,
        })
    case 'GET_COMMIT_HISTORY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCommitHistoryData: action.data,
            getCommitHistoryIsFinished: true,
            getCommitHistoryIsLoading: false,
        })
    default:
        return state
    }
}

export default student