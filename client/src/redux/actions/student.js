import genericDispatch from './fetch'

export function setCurrentStudent(student) {
    return {
        type: 'SET_CURRENT_STUDENT',
        student
    }
}

export function clearStudent() {
    return {
        type: 'CLEAR_STUDENT'
    }
}

export function getStudentHasError(hasError) {
    return {
        type: 'GET_STUDENT_HAS_ERROR',
        hasError
    }
}

export function getStudentDataSuccess(data) {
    return {
        type: 'GET_STUDENT_DATA_SUCCESS',
        data
    }
}

export const getStudent = genericDispatch(
    'GET_STUDENT', getStudentHasError, getStudentDataSuccess, 'GET'
)

export function getProgressLineHasError(hasError) {
    return {
        type: 'GET_PROGRESS_LINE_HAS_ERROR',
        hasError
    }
}

export function getProgressLineDataSuccess(data) {
    return {
        type: 'GET_PROGRESS_LINE_DATA_SUCCESS',
        data
    }
}

export const getProgressLine = genericDispatch(
    'GET_PROGRESS_LINE', getProgressLineHasError, getProgressLineDataSuccess, 'GET'
)

export function getCodeFrequencyHasError(hasError) {
    return {
        type: 'GET_CODE_FREQUENCY_HAS_ERROR',
        hasError
    }
}

export function getCodeFrequencyDataSuccess(data) {
    return {
        type: 'GET_CODE_FREQUENCY_DATA_SUCCESS',
        data
    }
}

export const getCodeFrequency = genericDispatch(
    'GET_CODE_FREQUENCY', getCodeFrequencyHasError, getCodeFrequencyDataSuccess, 'GET'
)

export function getCommitFrequencyHasError(hasError) {
    return {
        type: 'GET_COMMIT_FREQUENCY_HAS_ERROR',
        hasError
    }
}

export function getCommitFrequencyDataSuccess(data) {
    return {
        type: 'GET_COMMIT_FREQUENCY_DATA_SUCCESS',
        data
    }
}

export const getCommitFrequency = genericDispatch(
    'GET_COMMIT_FREQUENCY', getCommitFrequencyHasError, getCommitFrequencyDataSuccess, 'GET'
)

export function getStatisticsHasError(hasError) {
    return {
        type: 'GET_STATISTICS_HAS_ERROR',
        hasError
    }
}

export function getStatisticsDataSuccess(data) {
    return {
        type: 'GET_STATISTICS_DATA_SUCCESS',
        data
    }
}

export const getStatistics = genericDispatch(
    'GET_STATISTICS', getStatisticsHasError, getStatisticsDataSuccess, 'GET'
)

export function getCommitHistoryHasError(hasError) {
    return {
        type: 'GET_COMMIT_HISTORY_HAS_ERROR',
        hasError
    }
}

export function getCommitHistoryDataSuccess(data) {
    return {
        type: 'GET_COMMIT_HISTORY_DATA_SUCCESS',
        data
    }
}

export const getCommitHistory = genericDispatch(
    'GET_COMMIT_HISTORY', getCommitHistoryHasError, getCommitHistoryDataSuccess, 'GET'
)

export function getProgressPerTimeHasError(hasError) {
    return {
        type: 'GET_PROGRESS_PER_TIME_HAS_ERROR',
        hasError
    }
}

export function getProgressPerTimeDataSuccess(data) {
    return {
        type: 'GET_PROGRESS_PER_TIME_DATA_SUCCESS',
        data
    }
}

export const getProgressPerTime = genericDispatch(
    'GET_PROGRESS_PER_TIME', getProgressPerTimeHasError, getProgressPerTimeDataSuccess, 'GET'
)

export function getProgressPerCommitHasError(hasError) {
    return {
        type: 'GET_PROGRESS_PER_COMMIT_HAS_ERROR',
        hasError
    }
}

export function getProgressPerCommitDataSuccess(data) {
    return {
        type: 'GET_PROGRESS_PER_COMMIT_DATA_SUCCESS',
        data
    }
}

export const getProgressPerCommit = genericDispatch(
    'GET_PROGRESS_PER_COMMIT', getProgressPerCommitHasError, getProgressPerCommitDataSuccess, 'GET'
)