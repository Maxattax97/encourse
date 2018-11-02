function teachingAssistant(state = {}, action) {
    switch(action.type) {
    case 'GET_TEACHING_ASSISTANTS':
        return Object.assign({}, state, {
            getTeachingAssistantsIsLoading: true,
        })
    case 'GET_TEACHING_ASSISTANTS_HAS_ERROR':
        return Object.assign({}, state, {
            getTeachingAssistantsHasError: action.hasError,
            getTeachingAssistantsIsLoading: false,
        })
    case 'GET_TEACHING_ASSISTANTS_SUCCESS':
        return Object.assign({}, state, {
            getTeachingAssistantsData: action.data,
            getTeachingAssistantsIsLoading: false,
        })
    default:
        return state
    }
}

export default teachingAssistant