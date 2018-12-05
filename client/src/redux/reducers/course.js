import {forwardData, getData, unknownAction} from './reducer-utils'

function setCurrentCourse(state, action) {
    return Object.assign({}, state, {
        currentCourseId: action.id,
    })
}

function setCurrentSemester(state, action) {
    return Object.assign({}, state, {
        currentSemesterId: action.id,
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
    let content = state.students && state.students.data ? [...state.students.data] : []
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
    return content
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

const statDict = {
	"Average Additions": "Average number of line of code added over the entire project",
	"Average Deletions": "Average number of line of code deleted over the entire project",
	"Average Commit Count": "Average number of commits",
	"Average Estimated Time Spent": "Average estimated time spent. Estimated by recording the time between commits that appear less than an hour appart",
	"Start Date": "The date of the student's first commit",
	"End Date": "The date of the student's last commit",
	"Estimated Time Spent": "Estimated time spent on the project. Estimated by (recording an hour of work if there is a commit between two hours | recording time if there is a commit within an hour of another commit)",
	"Current Test Score": "Score calculated by summing the points given for each passing test case",
	"Commit Count": "Number of commits made the by student so far",
	"Deletions": "Number of deleted lines of code made the by student so far",
	"Additions": "Number of added lines of code made the by student so far",
    "Average Additions": "Average number of line of code added over the entire project",
    "Average Deletions": "Average number of line of code deleted over the entire project",
    "Average Commit Count": "Average number of commits",
    "Average Estimated Time Spent": "Average estimated time spent. Estimated by recording the time between commits that appear less than an hour appart",
}

function formatStatistics(udata) {
    if (!udata)
        return null

    udata.sort((d1, d2) => d1.index - d2.index)

    for (let item of udata) {
        if (statDict[item.stat_name]) {
            item.stat_desc = statDict[item.stat_name]
        }
    }

    return udata
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

function formatProgress(udata) {
    if (!udata)
        return []

    const data = udata
    const formattedData = []
    const data2 = Object.entries(data)
    const total = data2.reduce((sum, p) => sum + p[1], 0)

    for (let apiEntry of data2) {
        const entry = {
            progressBin: apiEntry[0],
            order: parseInt(apiEntry[0], 10),
            count: apiEntry[1],
            percent: apiEntry[1] / total,
        }

        formattedData.push(entry)
    }

    formattedData.sort((a, b) => a.order - b.order)

    return formattedData
}

function formatTestProgress(udata) {
    if(!udata)
        return []

    const data = udata
    const formattedData = []

    for (let apiEntry of data) {
        const entry = {
            testName: apiEntry.testName,
            hidden: apiEntry.hidden,
            percent: apiEntry.score / 100,
            score: apiEntry.score,
        }

        formattedData.push(entry)
    }

    return formattedData
}

function formatStudentsSimilarity(udata) {
    if(!udata)
        return []
    let data = udata
    for (let item of data) {
        item.similarity_bin *= 10
    }
    data.sort((a, b) => {
        if (a.similarity_bin === b.similarity_bin) {
            return a.height - b.height
        }
        return a.similarity_bin - b.similarity_bin
    })
    return data
}

function setCurrentTA(state, action) {
    return {
        ...state,
        currentTA: action.ta,
        currentTAIndex: action.index
    }
}

export default function course(state = {}, action) {
    if(action.class !== 'COURSE')
        return state

    switch(action.type) {
        case 'SET_CURRENT_COURSE':
            return setCurrentCourse(state, action)
        case 'SET_CURRENT_SEMESTER':
            return setCurrentSemester(state, action)
        case 'GET_STUDENTS':
            return forwardData(state, action, 'students', formatStudents, true)
        case 'GET_SECTIONS':
            return forwardData(state, action, 'sections')
        case 'GET_PROGRESS':
            return forwardData(state, action, 'studentsProgress', formatProgress)
        case 'GET_TEST_BAR_GRAPH':
            return forwardData(state, action, 'studentsTestProgress', formatTestProgress)
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
            return forwardData(state, action, 'studentsSimilarity', formatStudentsSimilarity)
        case 'GET_STATISTICS':
            return forwardData(state, action, 'stats', formatStatistics)
        case 'SUBMIT_STUDENTS':
            return submitStudents(state, action)
        case 'GET_COMMIT_HISTORY':
            return getClassCommitHistory(state, action)
        case 'GET_PROGRESS_ANON':
            return forwardData(state, action, 'courseProgress', formatProgress)
        case 'GET_TEST_BAR_GRAPH_ANON':
            return forwardData(state, action, 'courseTestProgress', formatTestProgress)
        case 'GET_TEACHING_ASSISTANTS':
            return forwardData(state, action, 'teachingAssistants')
        case 'SET_CURRENT_TA':
            return setCurrentTA(state, action)
        default:
            return unknownAction(state, action)
    }
}
