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
    case 'SUBMIT_STUDENTS':
        return Object.assign({}, state, {
            submitStudentsIsLoading: true,
        })
    case 'SUBMIT_STUDENTS_HAS_ERROR':
        return Object.assign({}, state, {
            submitStudentsHasError: action.hasError,
            submitStudentsIsLoading: false,
        })
    case 'SUBMIT_STUDENTS_SUCCESS':
        console.log(action.data)
        return Object.assign({}, state, {
            submitStudentsData: action.data,
            submitStudentsIsLoading: false,
        })
    default:
        return state
    }
}

export default teachingAssistant