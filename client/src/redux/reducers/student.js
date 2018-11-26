import {getData} from "./reducer-utils"

function setCurrentStudent(state, action) {
	return Object.assign({}, state, {
		currentStudent: action.student,
	})
}

function clearStudent(state, action) {
	return Object.assign({}, state, {
		currentStudent: null,
		getProgressLineData: null,
		getCommitFrequencyData: null,
		getCodeFrequencyData: null,
		getStatisticsData: null,
		getCommitHistoryData: null,
	})
}

function getStudent(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    getStudentHasError: action.hasError,
		    getStudentIsLoading: false,
	    })
    if(action.data)
	    return Object.assign({}, state, {
		    getStudentData: action.data,
		    currentStudent: action.data,
		    getStudentIsLoading: false,
	    })
	return Object.assign({}, state, {
		getStudentIsLoading: true,
	})
}

function getCommitHistory(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    getCommitHistoryHasError: action.hasError,
		    getCommitHistoryIsLoading: false,
	    })
    if(action.data) {
	    let content = state.getCommitHistoryData ? [...state.getCommitHistoryData.content] : []
	    let contains = false
	    for(let value of content) {
		    if(value.date === action.data.content[0].date) {
			    contains = true
			    break
		    }
	    }
	    if(!contains) {
		    content = content.concat(action.data.content)
	    }
	    action.data.content = content;
	    return Object.assign({}, state, {
		    getCommitHistoryData: action.data,
		    getCommitHistoryIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		getCommitHistoryIsLoading: true,
	})
}

function updateCommitsPage(state, action) {
	return Object.assign({}, state, {
		commitsPage: state.commitsPage ? state.commitsPage + 1 : 2,
	})
}

function resetCommitsPage(state, action) {
	return Object.assign({}, state, {
		commitsPage: 1,
	})
}

export default function student(state = {}, action) {
    if(action.type !== 'STUDENT')
        return state

    switch(action.class) {
    case 'SET_CURRENT_STUDENT':
        return setCurrentStudent(state, action)
    case 'CLEAR_STUDENT':
        return clearStudent(state, action)
    case 'GET_STUDENT':
        return getStudent(state, action)
    case 'GET_PROGRESS_LINE':
        return getData(state, action, 'getProgressLine')
    case 'GET_COMMIT_FREQUENCY':
        return getData(state, action, 'getCommitFrequency')
    case 'GET_CODE_FREQUENCY':
        return getData(state, action, 'getCodeFrequency')
    case 'GET_STATISTICS':
        return getData(state, action, 'getStatistics')
    case 'GET_COMMIT_HISTORY':
        return getCommitHistory(state, action)
    case 'GET_PROGRESS_PER_TIME':
        return getData(state, action, 'getProgressPerTime')
    case 'GET_PROGRESS_PER_COMMIT':
        return getData(state, action, 'getProgressPerCommit')
    case 'UPDATE_COMMITS_PAGE':
        return updateCommitsPage(state, action)
    case 'RESET_COMMITS_PAGE':
        return resetCommitsPage(state, action)
    case 'SYNC_STUDENT_REPOSITORY':
        return getData(state, action, 'syncStudentRepository')
    case 'RUN_STUDENT_TESTS':
        return getData(state, action, 'runStudentTests')
    default:
	    return Object.assign({}, state, {
		    reduxError: action
	    })
    }
}