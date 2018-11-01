import genericDispatch from './fetch'

export function getTeachingAssistantsHasError(hasError) {
    return {
        type: 'GET_TEACHING_ASSISTANTS_HAS_ERROR',
        hasError
    }
}

export function getTeachingAssistantsSuccess(data) {

    return {
        type: 'GET_TEACHING_ASSISTANTS_SUCCESS',
        data
    }
}

export const getTeachingAssistants = genericDispatch(
    'GET_TEACHING_ASSISTANTS', getTeachingAssistantsHasError, getTeachingAssistantsSuccess, 'GET'
)

export function submitStudentsHasError(hasError) {
    return {
        type: 'SUBMIT_STUDENTS_HAS_ERROR',
        hasError
    }
}

export function submitStudentsSuccess(data) {

    return {
        type: 'SUBMIT_STUDENTS_SUCCESS',
        data
    }
}

export const submitStudents = genericDispatch(
    'SUBMIT_STUDENTS', submitStudentsHasError, submitStudentsSuccess, 'POST'
)