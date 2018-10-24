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