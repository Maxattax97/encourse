import {getData} from "./reducer-utils"

function getStudentPreviews(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    getStudentPreviewsHasError: action.hasError,
		    getStudentPreviewsIsLoading: false,
	    })
    if(action.data) {
        /*
         * TODO Jordan Buckmaster: this should replace and resort. To be precise, replacing is such that any time a value
         * is already present inside inside the getStudentPreviewsData variable, we replace with the newer copy.
         * This shouldn't be a hard coded (first element of the new data check) approach because it could be such that
         * the result is larger the getStudentPreviewsData contains. Resort is the idea that we aren't ensuring what we've
         * retrieved is sorted. So, we should rerun the operation on the front-end as well to double check that things go
         * as expected. I've already noticed problems on the front-end because of this (this is a two-fold problem with the
         * back-end involved as well).
        */
	    let content = state.getStudentPreviewsData ? [...state.getStudentPreviewsData.content] : []
	    let contains = false
	    for(let value of content) {
		    if(value.id === action.data.content[0].id) {
			    contains = true
			    break
		    }
	    }
	    if(!contains) {
		    content = content.concat(action.data.content)
	    }
	    action.data.content = content
	    return Object.assign({}, state, {
		    getStudentPreviewsData: action.data,
		    getStudentPreviewsIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		getStudentPreviewsIsLoading: true,
	})
}

function getDishonestyReport(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    getDishonestyReportHasError: action.hasError,
		    getDishonestyReportIsLoading: false,
	    })
    if(action.data) {
	    /*
 * TODO Jordan Buckmaster: this should replace and resort. To be precise, replacing is such that any time a value
 * is already present inside inside the getStudentPreviewsData variable, we replace with the newer copy.
 * This shouldn't be a hard coded (first element of the new data check) approach because it could be such that
 * the result is larger the getStudentPreviewsData contains. Resort is the idea that we aren't ensuring what we've
 * retrieved is sorted. So, we should rerun the operation on the front-end as well to double check that things go
 * as expected. I've already noticed problems on the front-end because of this (this is a two-fold problem with the
 * back-end involved as well).
*/
	    let content = state.getDishonestyReportData ? [...state.getDishonestyReportData.content] : []
	    let contains = false
	    for(let value of content) {
		    if(value.id === action.data.content[0].id) {
			    contains = true
			    break
		    }
	    }
	    if(!contains) {
		    content = content.concat(action.data.content)
	    }
	    action.data.content = content;
	    return Object.assign({}, state, {
		    getDishonestyReportData: action.data,
		    getDishonestyReportIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		getDishonestyReportIsLoading: true,
	})
}

function updateCourseDishonestyPage(state, action) {
	return Object.assign({}, state, {
		dishonestyPage: state.dishonestyPage ? state.dishonestyPage + 1 : 2,
	})
}

function resetCourseDishonestyPage(state, action) {
	return Object.assign({}, state, {
		getDishonestyReportData: null,
		dishonestyPage: 1,
	})
}

function updateStudentsPage(state, action) {
	return Object.assign({}, state, {
		studentsPage: state.studentsPage ? state.studentsPage + 1 : 2,
	})
}

function resetStudentsPage(state, action) {
	return Object.assign({}, state, {
		getStudentPreviewsData: null,
		studentsPage: 1,
	})
}

function submitStudents(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    submitStudentsHasError: action.hasError,
		    submitStudentsIsLoading: false,
	    })
    if(action.data) {
	    let ta = action.extra.ta
	    let updatedStudents = action.extra.students
	    let students = {...state.getStudentPreviewsData}
	    for(let student of updatedStudents) {
		    for(let student2 of students.content) {
			    if(student === student2.id) {
				    if(!student2.teaching_assistants.includes(ta)) {
					    student2.teaching_assistants.push(ta)
				    }
			    }
		    }
	    }
	    return Object.assign({}, state, {
		    submitStudentsData: action.data,
		    submitStudentsIsLoading: false,
		    getStudentPreviewsData: students,
	    })
    }
	return Object.assign({}, state, {
		submitStudentsIsLoading: true,
	})
}

function getClassCommitHistory(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    getClassCommitHistoryHasError: action.hasError,
		    getClassCommitHistoryIsLoading: false,
	    })
    if(action.data) {
	    let content = state.getClassCommitHistoryData ? [...state.getClassCommitHistoryData.content] : []
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
		    getClassCommitHistoryData: action.data,
		    getClassCommitHistoryIsLoading: false,
	    })
    }
	return Object.assign({}, state, {
		getClassCommitHistoryIsLoading: true,
	})
}

export default function course(state = {}, action) {
    if(action.type !== 'COURSE')
        return state

    switch(action.class) {
    case 'GET_STUDENTS':
        return getStudentPreviews(state, action)
    case 'GET_SECTIONS':
        return getData(state, action, 'getSectionsData')
    case 'GET_PROGRESS':
        return getData(state, action, 'getClassProgress')
    case 'GET_TEST_BAR_GRAPH':
        return getData(state, action, 'getTestBarGraph')
    case 'SET_DIRECTORY':
        return getData(state, action, 'setDirectory')
	case 'SYNC':
	    return getData(state, action, 'syncRepositories')
    case 'RUN_TESTS':
        return getData(state, action, 'runTests')
    case 'GET_DISHONESTY_REPORT':
        return getDishonestyReport(state, action)
    case 'UPDATE_COURSE_DISHONESTY_PAGE':
        return updateCourseDishonestyPage(state, action)
    case 'RESET_COURSE_DISHONESTY_PAGE':
        return resetCourseDishonestyPage(state, action)
    case 'UPDATE_STUDENTS_PAGE':
        return updateStudentsPage(state, action)
    case 'RESET_STUDENTS_PAGE':
        return resetStudentsPage(state, action)
    case 'GET_SIMILARITY_PLOT':
        return getData(state, action, 'getSimilarityPlot')
    case 'GET_STATISTICS':
        return getData(state, action, 'getClassStatistics')
    case 'SUBMIT_STUDENTS':
        return submitStudents(state, action)
    case 'GET_COMMIT_HISTORY':
        return getClassCommitHistory(state, action)
    case 'GET_PROGRESS_ANON':
        return getData(state, action, 'getClassProgressAnon')
    case 'GET_TEST_BAR_GRAPH_ANON':
        return getData(state, action, 'getTestBarGraphAnon')
    default:
	    return Object.assign({}, state, {
		    reduxError: action
	    })
    }
}