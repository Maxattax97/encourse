import {forwardData, unknownAction} from './reducer-utils'

function addCourse(data, extra, state) {
    state.courses.data = [...state.courses.data, data]

    return data
}

//TODO Bucky, change course.userID to a unique identiifer
function modifyCourse(data, extra, state) {
    state.courses.data = [...state.courses.data.filter(course => course.userID !== data.userID), data]

    return data
}

function removeCourse(data, extra, state) {
    state.courses.data = [...state.courses.data.filter(course => course.userID !== data.userID)]

    return data
}

function addAccount(data, extra, state) {
    state.accounts.data = [...state.accounts.data, data]

    return data
}

function modifyAccount(data, extra, state) {
    state.accounts.data = [...state.accounts.data.filter(account => account.userID !== data.userID), data]

    return data
}

function removeAccount(data, extra, state) {
    state.accounts.data = [...state.accounts.data.filter(account => account.userID !== data.userID)]

    return data
}

export default function admin(state = {}, action) {
	if(action.class !== 'ADMIN')
		return state;

	switch(action.type) {
        case 'GET_COURSES':
            return forwardData(state, action, 'courses')
        case 'ADD_COURSE':
            return forwardData(state, action, 'addCourse', addCourse)
        case 'MODIFY_COURSE':
            return forwardData(state, action, 'modifyCourse', modifyCourse)
        case 'REMOVE_COURSE':
            return forwardData(state, action, 'removeCourse', removeCourse)
        case 'GET_ACCOUNTS':
            return forwardData(state, action, 'accounts')
        case 'ADD_ACCOUNT':
            return forwardData(state, action, 'addAccount', addAccount)
        case 'MODIFY_ACCOUNT':
            return forwardData(state, action, 'modifyAccount', modifyAccount)
        case 'REMOVE_ACCOUNT':
            return forwardData(state, action, 'removeAccount', removeAccount)
        default:
			return unknownAction(state, action)
	}
}