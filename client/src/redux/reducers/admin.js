import {getData} from "./reducer-utils"

function addCourse(state, action) {
	if(action.error)
		return Object.assign({}, state, {
			addCourseHasError: action.error,
			addCourseIsLoading: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			addCourseData: action.data,
			getCoursesData: [...state.getCoursesData, action.data],
			addCourseIsLoading: false,
		})
	return Object.assign({}, state, {
		addCourseIsLoading: true,
	})
}

function modifyCourse(state, action) {
	if(action.error)
		return Object.assign({}, state, {
			modifyCourseHasError: action.error,
			modifyCourseIsLoading: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			modifyCourseData: action.data,
			modifyCourseIsLoading: false,
		})
	return Object.assign({}, state, {
		modifyCourseIsLoading: true,
	})
}

function removeCourse(state, action) {
	if(action.error)
		return Object.assign({}, state, {
			removeCourseHasError: action.error,
			removeCourseIsLoading: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			removeCourseData: action.data,
			removeCourseIsLoading: false,
		})
	return Object.assign({}, state, {
		removeCourseIsLoading: true,
	})
}

function addAccount(state, action) {
	if(action.error)
		return Object.assign({}, state, {
			addAccountHasError: action.error,
			addAccountIsLoading: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			addAccountData: action.data,
			getAccountsData: [...state.getAccountsData, action.data],
			addAccountIsLoading: false,
		})
	return Object.assign({}, state, {
		addAccountIsLoading: true,
	})
}

function modifyAccount(state, action) {
	if(action.error)
		return Object.assign({}, state, {
			modifyAccountHasError: action.error,
			modifyAccountIsLoading: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			modifyAccountData: action.data,
			getAccountsData: [...state.getAccountsData.filter(account => account.userID !== action.data.userID), action.data],
			modifyAccountIsLoading: false,
		})
	return Object.assign({}, state, {
		modifyAccountIsLoading: true,
	})
}

function removeAccount(state, action) {
	if(action.error)
		return Object.assign({}, state, {
			removeAccountHasError: action.error,
			removeAccountIsLoading: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			removeAccountData: action.data,
			getAccountsData: [...state.getAccountsData.filter(account => account.userID !== action.data.userID)],
			removeAccountIsLoading: false,
		})
	return Object.assign({}, state, {
		removeAccountIsLoading: true,
	})
}

export default function admin(state = {}, action) {
	if(action.class !== 'ADMIN')
		return state;

	switch(action.type) {
        case 'GET_COURSES':
            return getData(state, action, 'getCourses')
        case 'ADD_COURSE':
            return addCourse(state, action)
        case 'MODIFY_COURSE':
            return modifyCourse(state, action)
        case 'REMOVE_COURSE':
            return removeCourse(state, action)
        case 'GET_ACCOUNTS':
            return getData(state, action, 'getAccounts')
        case 'ADD_ACCOUNT':
            return addAccount(state, action)
        case 'MODIFY_ACCOUNT':
            return modifyAccount(state, action)
        case 'REMOVE_ACCOUNT':
            return removeAccount(state, action)
		default:
			return Object.assign({}, state, {
			    reduxError: action
            })
	}
}