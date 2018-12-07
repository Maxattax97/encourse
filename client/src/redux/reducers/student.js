import {forwardData, getData, resetData, unknownAction} from "./reducer-utils"
import moment from "moment"

function setCurrentStudent(state, action) {
	return {
		...state,
		student: {
		    loading: false,
            data: action.student,
            error: null
        }
	}
}

function clearStudent(state, action) {
	return {
		...state,
		...resetData('student'),
		...resetData('studentProgress'),
		...resetData('commitFrequency'),
		...resetData('codeChanges'),
		...resetData('commitHistory'),
		...resetData('getProgressPerTime'),
		...resetData('stats'),
		...resetData('getProgressPerCommit'),
	}
}

function formatCommitFrequency(udata) {
	if (!udata) {
		return []
	}
	let data = udata

	if (!data || data.length === 0) {
		return []
	}

	for (let entry of data) {
		entry.date = moment(entry.date).valueOf()
	}

	return data
}

function formatCodeChanges(udata) {
	if (!udata || !udata) {
		return []
	}
	const data = udata

	for (let entry of data) {
		entry.date = moment(entry.date).valueOf()
		entry.deletions = -entry.deletions
	}

	if (!data || data.length === 0) {
		return []
	}

	const minDate = data.reduce((min, p) => p.date < min ? p.date : min, data[0].date)
	const maxDate = data.reduce((max, p) => p.date > max ? p.date : max, data[0].date)

	const formattedData = []

	let inputIndex = 0
	for (let m = moment(minDate); m.diff(moment(maxDate), 'days') <= 0; m.add(1, 'days')) {
		const inputEntry = data[inputIndex]
		const inputDate = inputEntry.date

		if (m.isSame(inputDate, 'day')) {
			formattedData.push(inputEntry)
			inputIndex++
		}
		else {
			formattedData.push({
				date: m.valueOf(),
				additions: data[inputIndex - 1] || 0,
				deletions: data[inputIndex - 1] || 0,
			})
		}
	}

	return formattedData
}

function formatStudentProgress(udata) {
	if (!udata || !udata) {
		return []
	}
	const data = udata
	for (let entry of data) {
		entry.date = moment(entry.date).valueOf()
	}

	if (!data || data.length === 0) {
		return []
	}

	const minDate = data.reduce((min, p) => p.date < min ? p.date : min, data[0].date)
	const maxDate = data.reduce((max, p) => p.date > max ? p.date : max, data[0].date)

	const formattedData = []

	let inputIndex = 0
	for (let m = moment(minDate); m.diff(moment(maxDate), 'days') <= 0; m.add(1, 'days')) {
		const inputEntry = data[inputIndex]
		const inputDate = inputEntry.date

		if (m.isSame(inputDate, 'day')) {
			formattedData.push(inputEntry)
			inputIndex++
		}
		else {
			formattedData.push({
				date: m.valueOf(),
				progress: inputEntry.progress,
			})
		}
	}

	return formattedData
}

function sortStatistics(udata) {
	if (!udata)
		return []

	const data = udata[0]

	data.sort((d1, d2) => d1.index - d2.index)

	return data
}

function formatCommitHistory(udata, extra, state) {
	if(!udata)
		return {}

	let content = state.commitHistory && state.commitHistory.data ? [...state.commitHistory.data] : []

	let contains = false
	for(let value of content) {
		if(value.date === udata[0].date) {
			contains = true
			break
		}
	}
	if(!contains)
		content = content.concat(udata)

	return content
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
    if(action.class !== 'STUDENT')
        return state

    switch(action.type) {
    case 'SET_CURRENT':
        return setCurrentStudent(state, action)
    case 'CLEAR':
        return clearStudent(state, action)
    case 'GET':
        return forwardData(state, action, 'student')
    case 'GET_PROGRESS_LINE':
        return forwardData(state, action, 'studentProgress', formatStudentProgress)
    case 'GET_COMMIT_FREQUENCY':
        return forwardData(state, action, 'commitFrequency', formatCommitFrequency)
    case 'GET_CODE_FREQUENCY':
        return forwardData(state, action, 'codeChanges', formatCodeChanges)
    case 'GET_STATISTICS':
        return forwardData(state, action, 'stats', sortStatistics)
    case 'GET_COMMIT_HISTORY':
        return forwardData(state, action, 'commitHistory', formatCommitHistory, true)
    case 'GET_PROGRESS_PER_TIME':
        return getData(state, action, 'getProgressPerTime')
    case 'GET_PROGRESS_PER_COMMIT':
        return getData(state, action, 'getProgressPerCommit')
    case 'UPDATE_COMMITS_PAGE':
        return updateCommitsPage(state, action)
    case 'RESET_COMMITS_PAGE':
        return resetCommitsPage(state, action)
    case 'SYNC':
        return getData(state, action, 'syncStudentRepository')
    case 'RUN_TESTS':
		return getData(state, action, 'runStudentTests')
	case 'GET_SOURCE':
		return getData(state, action, 'getSource')
    default:
        return unknownAction(state, action)
    }
}