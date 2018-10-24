function admin(state = {}, action) {
    let accounts
    let courses
    switch(action.type) {
    case 'GET_COURSES':
        return Object.assign({}, state, {
            getCoursesIsLoading: true,
        })
    case 'GET_COURSES_HAS_ERROR':
        return Object.assign({}, state, {
            getCoursesHasError: action.hasError,
            getCoursesIsLoading: false,
        })
    case 'GET_COURSES_DATA_SUCCESS':
        return Object.assign({}, state, {
            getCoursesData: action.data,
            getCoursesIsLoading: false,
        })
    case 'ADD_COURSE':
        return Object.assign({}, state, {
            addCourseIsLoading: true,
        })
    case 'ADD_COURSE_HAS_ERROR':
        return Object.assign({}, state, {
            addCourseHasError: action.hasError,
            addCourseIsLoading: false,
        })
    case 'ADD_COURSE_DATA_SUCCESS':
        courses = [...state.getCoursesData]
        courses.push(action.data)
        return Object.assign({}, state, {
            addCourseData: action.data,
            getCoursesData: courses,
            addCourseIsLoading: false,
        })
    case 'MODIFY_COURSE':
        return Object.assign({}, state, {
            modifyCourseIsLoading: true,
        })
    case 'MODIFY_COURSE_HAS_ERROR':
        return Object.assign({}, state, {
            modifyCourseHasError: action.hasError,
            modifyCourseIsLoading: false,
        })
    case 'MODIFY_COURSE_DATA_SUCCESS':
        return Object.assign({}, state, {
            modifyCourseData: action.data,
            modifyCourseIsLoading: false,
        })
    case 'REMOVE_COURSE':
        return Object.assign({}, state, {
            removeCourseIsLoading: true,
        })
    case 'REMOVE_COURSE_HAS_ERROR':
        return Object.assign({}, state, {
            removeCourseHasError: action.hasError,
            removeCourseIsLoading: false,
        })
    case 'REMOVE_COURSE_DATA_SUCCESS':
        return Object.assign({}, state, {
            removeCourseData: action.data,
            removeCourseIsLoading: false,
        })
    case 'GET_ACCOUNTS':
        return Object.assign({}, state, {
            getAccountsIsLoading: true,
        })
    case 'GET_ACCOUNTS_HAS_ERROR':
        return Object.assign({}, state, {
            getAccountsHasError: action.hasError,
            getAccountsIsLoading: false,
        })
    case 'GET_ACCOUNTS_DATA_SUCCESS':
        return Object.assign({}, state, {
            getAccountsData: action.data,
            getAccountsIsLoading: false,
        })
    case 'ADD_ACCOUNT':
        return Object.assign({}, state, {
            addAccountIsLoading: true,
        })
    case 'ADD_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            addAccountHasError: action.hasError,
            addAccountIsLoading: false,
        })
    case 'ADD_ACCOUNT_DATA_SUCCESS':
        accounts = [...state.getAccountsData]
        accounts.push(action.data)
        return Object.assign({}, state, {
            addAccountData: action.data,
            getAccountsData: accounts,
            addAccountIsLoading: false,
        })
    case 'MODIFY_ACCOUNT':
        return Object.assign({}, state, {
            modifyAccountIsLoading: true,
        })
    case 'MODIFY_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            modifyAccountHasError: action.hasError,
            modifyAccountIsLoading: false,
        })
    case 'MODIFY_ACCOUNT_DATA_SUCCESS':
        accounts = [...state.getAccountsData]
        let id1 = action.data.userID
        for(let i = 0; i < accounts.length; i++) {
            if(accounts[i].userID === id1) {
                accounts[i] = action.data
                break
            }
        }
        return Object.assign({}, state, {
            modifyAccountData: action.data,
            getAccountsData: accounts,
            modifyAccountIsLoading: false,
        })
    case 'REMOVE_ACCOUNT':
        return Object.assign({}, state, {
            removeAccountIsLoading: true,
        })
    case 'REMOVE_ACCOUNT_HAS_ERROR':
        return Object.assign({}, state, {
            removeAccountHasError: action.hasError,
            removeAccountIsLoading: false,
        })
    case 'REMOVE_ACCOUNT_DATA_SUCCESS':
        accounts = [...state.getAccountsData]
        let id2 = action.data.userID
        for(let i = 0; i < accounts.length; i++) {
            if(accounts[i].userID === id2) {
                accounts.splice(i, 1)
                break
            }
        }
        return Object.assign({}, state, {
            removeAccountData: action.data,
            getAccountsData: accounts,
            removeAccountIsLoading: false,
        })
    default:
        return state
    }
}

export default admin