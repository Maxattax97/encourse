function admin(state = {}, action) {
    switch(action.type) {
    case 'GET_COURSES_HAS_ERROR':
        return Object.assign({}, state, {
            getCoursesHasError: action.hasError,
        })
    case 'GET_COURSES_IS_LOADING':
        return Object.assign({}, state, {
            getCoursesIsLoading: action.isLoading,
        })
    case 'GET_COURSES_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCoursesData: action.data,
        })
    case 'ADD_COURSE_HAS_ERROR':
        return Object.assign({}, state, {
            addCourseHasError: action.hasError,
        })
    case 'ADD_COURSE_IS_LOADING':
        return Object.assign({}, state, {
            addCourseIsLoading: action.isLoading,
        })
    case 'ADD_COURSE_DATA_SUCCESS':
        return Object.assign({}, state, {
            addCourseData: action.data,
        })
    case 'MODIFY_COURSE_HAS_ERROR':
        return Object.assign({}, state, {
            modifyCourseHasError: action.hasError,
        })
    case 'MODIFY_COURSE_IS_LOADING':
        return Object.assign({}, state, {
            modifyCourseIsLoading: action.isLoading,
        })
    case 'MODIFY_COURSE_DATA_SUCCESS':
        return Object.assign({}, state, {
            modifyCourseData: action.data,
        })
    case 'REMOVE_COURSE_HAS_ERROR':
        return Object.assign({}, state, {
            removeCourseHasError: action.hasError,
        })
    case 'REMOVE_COURSE_IS_LOADING':
        return Object.assign({}, state, {
            removeCourseIsLoading: action.isLoading,
        })
    case 'REMOVE_COURSE_DATA_SUCCESS':
        return Object.assign({}, state, {
            removeCourseData: action.data,
        })
    case 'GET_ACCOUNTS_HAS_ERROR':
        return Object.assign({}, state, {
            getAccountsHasError: action.hasError,
        })
    case 'GET_ACCOUNTS_IS_LOADING':
        return Object.assign({}, state, {
            getAccountsIsLoading: action.isLoading,
        })
    case 'GET_ACCOUNTS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getAccountsData: action.data,
        })
    case 'ADD_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            addAccountHasError: action.hasError,
        })
    case 'ADD_ACCOUNT_IS_LOADING':
        return Object.assign({}, state, {
            addAccountIsLoading: action.isLoading,
        })
    case 'ADD_ACCOUNT_DATA_SUCCESS':
        return Object.assign({}, state, {
            addAccountData: action.data,
        })
    case 'MODIFY_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            modifyAccountHasError: action.hasError,
        })
    case 'MODIFY_ACCOUNT_IS_LOADING':
        return Object.assign({}, state, {
            modifyAccountIsLoading: action.isLoading,
        })
    case 'MODIFY_ACCOUNT_DATA_SUCCESS':
        let accounts1 = [...state.getAccountsData]
        let id1 = action.data.userID
        for(let i = 0; i < accounts1.length; i++) {
            if(accounts1[i].userID === id1) {
                accounts1[i] = action.data
                break
            }
        }
        return Object.assign({}, state, {
            modifyAccountData: action.data,
            getAccountsData: accounts1
        })
    case 'REMOVE_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            removeAccountHasError: action.hasError,
        })
    case 'REMOVE_ACCOUNT_IS_LOADING':
        return Object.assign({}, state, {
            removeAccountIsLoading: action.isLoading,
        })
    case 'REMOVE_ACCOUNT_DATA_SUCCESS':
        let accounts2 = [...state.getAccountsData]
        let id2 = action.data.userID
        for(let i = 0; i < accounts2.length; i++) {
            if(accounts2[i].userID === id2) {
                accounts2.splice(i, 1)
                break
            }
        }
        return Object.assign({}, state, {
            removeAccountData: action.data,
            getAccountsData: accounts2,
        })
    default:
        return state
    }
}

export default admin