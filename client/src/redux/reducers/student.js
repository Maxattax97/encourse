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
    case 'GET_STUDENT':
        return Object.assign({}, state, {
            getStudentIsLoading: true,
        })
    case 'GET_STUDENT_HAS_ERROR':
        return Object.assign({}, state, {
            getStudentHasError: action.hasError,
            getStudentIsLoading: false,
        })
    case 'GET_STUDENT_DATA_SUCCESS':
        return Object.assign({}, state, {
            getStudentData: action.data,
            getStudentIsLoading: false,
        })
    case 'GET_PROGRESS_LINE':
        return Object.assign({}, state, {
            getProgressLineIsLoading: true,
        })
    case 'GET_PROGRESS_LINE_HAS_ERROR':
        return Object.assign({}, state, {
            getProgressLineHasError: action.hasError,
            getProgressLineIsLoading: false,
        })
    case 'GET_PROGRESS_LINE_DATA_SUCCESS':
        return Object.assign({}, state, {
            getProgressLineData: action.data,
            getProgressLineIsLoading: false,
        })
    case 'GET_COMMIT_FREQUENCY':
        return Object.assign({}, state, {
            getCommitFrequencyIsLoading: true,
        })
    case 'GET_COMMIT_FREQUENCY_HAS_ERROR':
        return Object.assign({}, state, {
            getCommitFrequencyHasError: action.hasError,
            getCommitFrequencyIsLoading: false,
        })
    case 'GET_COMMIT_FREQUENCY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCommitFrequencyData: action.data,
            getCommitFrequencyIsLoading: false,
        })
    case 'GET_CODE_FREQUENCY':
        return Object.assign({}, state, {
            getCodeFrequencyIsLoading: true,
        })
    case 'GET_CODE_FREQUENCY_HAS_ERROR':
        return Object.assign({}, state, {
            getCodeFrequencyHasError: action.hasError,
            getCodeFrequencyIsLoading: false,
        })
    case 'GET_CODE_FREQUENCY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCodeFrequencyData: action.data,
            getCodeFrequencyIsLoading: false,
        })
    case 'GET_STATISTICS':
        return Object.assign({}, state, {
            getStatisticsIsLoading: true,
        })
    case 'GET_STATISTICS_HAS_ERROR':
        return Object.assign({}, state, {
            getStatisticsHasError: action.hasError,
            getStatisticsIsLoading: false,
        })
    case 'GET_STATISTICS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getStatisticsData: action.data,
            getStatisticsIsLoading: false,
        })
    case 'GET_COMMIT_HISTORY':
        return Object.assign({}, state, {
            getCommitHistoryIsLoading: true,
        })
    case 'GET_COMMIT_HISTORY_HAS_ERROR':
        return Object.assign({}, state, {
            getCommitHistoryHasError: action.hasError,
            getCommitHistoryIsLoading: false,
        })
    case 'GET_COMMIT_HISTORY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCommitHistoryData: action.data,
            getCommitHistoryIsLoading: false,
        })
    case 'GET_PROGRESS_VELOCITY':
        return Object.assign({}, state, {
            getProgressVelocityIsLoading: true,
        })
    case 'GET_PROGRESS_VELOCITY_HAS_ERROR':
        return Object.assign({}, state, {
            getProgressVelocityHasError: action.hasError,
            getProgressVelocityIsLoading: false,
        })
    case 'GET_PROGRESS_VELOCITY_DATA_SUCCESS':
        return Object.assign({}, state, {
            getProgressVelocityData: action.data,
            getProgressVelocityIsLoading: false,
        })
    default:
        return state
    }
}

export default student