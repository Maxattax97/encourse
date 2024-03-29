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

function setCurrentCommit(state, action) {
    return {
        ...state,
        currentCommit: {
            loading: false,
            data: action.commit,
            error: null
        }
    }
}

function clearCommit(state, action) {
    return {
        ...state,
        ...resetData('currentCommit'),
        ...resetData('commitData')
    }
}

function formatStudentCharts(udata) {
    if(!udata)
        return []

    udata.frequencies = udata.frequencies.map(frequency => {
        return {
            date: moment(frequency.date).format('M-D'),
            frequency: frequency.frequency
        }
    }).sort((a, b) => moment(a.date).diff(moment(b.date)))

    udata.commits = udata.commits.sort((a, b) => moment(a.date).diff(moment(b.date)))

    return udata
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

function formatStudents(udata, extra, state) {
    /*
     * TODO Jordan Buckmaster: this should replace and resort. To be precise, replacing is such that any time a value
     * is already present inside inside the getStudentPreviewsData variable, we replace with the newer copy.
     * This shouldn't be a hard coded (first element of the new data check) approach because it could be such that
     * the result is larger the getStudentPreviewsData contains. Resort is the idea that we aren't ensuring what we've
     * retrieved is sorted. So, we should rerun the operation on the front-end as well to double check that things go
     * as expected. I've already noticed problems on the front-end because of this (this is a two-fold problem with the
     * back-end involved as well).
    */

    /*let content = state.students && state.students.data ? [...state.students.data] : []
    if(udata.length < content.length) {
        content = udata
    } else {
        let contains = false
        for(let value of content) {
            if(value.id === udata[0].id) {
                contains = true
                break
            }
        }
        if(!contains) {
            content = content.concat(udata)
        }
    }*/


    return udata.sort((a, b) => {
        if(a.percent > b.percent)
            return -1
        if(a.percent < b.percent)
            return 1
        return 0
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
        case 'SET_CURRENT_COMMIT':
            return setCurrentCommit(state, action)
        case 'CLEAR_COMMIT':
            return clearCommit(state, action)
        case 'GET':
            return forwardData(state, action, 'student')
        case 'GET_STUDENT_COMPARISONS':
            return forwardData(state, action, 'studentComparisons', formatStudents)
        case 'GET_STUDENT_CHARTS':
            return forwardData(state, action, 'studentCharts', formatStudentCharts)
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
            return forwardData(state, action, 'source')
        default:
            return unknownAction(state, action)
    }
}