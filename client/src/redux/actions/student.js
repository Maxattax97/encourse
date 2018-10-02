import genericDispatch from './fetch'

export function setCurrentStudent(student) {
    return {
        type: 'SET_CURRENT_STUDENT',
        student
    }
}

export function getProgressLineHasError(hasError) {
    return {
        type: 'GET_PROGRESS_LINE_HAS_ERROR',
        hasError
    }
}

export function getProgressLineIsLoading(isLoading) {
    return {
        type: 'GET_PROGRESS_LINE_IS_LOADING',
        isLoading
    }
}

export function getProgressLineDataSuccess(data) {
    return {
        type: 'GET_PROGRESS_LINE_DATA_SUCCESS',
        data
    }
}

export const getProgressLine = genericDispatch(
    getProgressLineHasError, getProgressLineIsLoading, getProgressLineDataSuccess, 'GET'
)

export function getCodeFrequencyHasError(hasError) {
    return {
        type: 'GET_CODE_FREQUENCY_HAS_ERROR',
        hasError
    }
}

export function getCodeFrequencyIsLoading(isLoading) {
    return {
        type: 'GET_CODE_FREQUENCY_IS_LOADING',
        isLoading
    }
}

export function getCodeFrequencyDataSuccess(data) {
    return {
        type: 'GET_CODE_FREQUENCY_DATA_SUCCESS',
        data
    }
}

export const getCodeFrequency = genericDispatch(
    getCodeFrequencyHasError, getCodeFrequencyIsLoading, getCodeFrequencyDataSuccess, 'GET'
)

export function getCommitFrequencyHasError(hasError) {
    return {
        type: 'GET_COMMIT_FREQUENCY_HAS_ERROR',
        hasError
    }
}

export function getCommitFrequencyIsLoading(isLoading) {
    return {
        type: 'GET_COMMIT_FREQUENCY_IS_LOADING',
        isLoading
    }
}

export function getCommitFrequencyDataSuccess(data) {
    return {
        type: 'GET_COMMIT_FREQUENCY_DATA_SUCCESS',
        data
    }
}

export const getCommitFrequency = genericDispatch(
    getCommitFrequencyHasError, getCommitFrequencyIsLoading, getCommitFrequencyDataSuccess, 'GET'
)