import {genericDispatch} from "./fetch"

const class_type = 'COURSE'
export function setCurrentCourse(id) {
    return {
        class: class_type,
        type: 'SET_CURRENT_COURSE',
        id
    }
}

export function setCurrentSemester(id) {
    return {
        class: class_type,
        type: 'SET_CURRENT_COURSE',
        id
    }
}

export function updateCourseDishonestyPage() {
    return {
        class: class_type,
        type: 'UPDATE_COURSE_DISHONESTY_PAGE'
    }
}

export function resetCourseDishonestyPage() {
    return {
	    class: class_type,
	    type: 'RESET_COURSE_DISHONESTY_PAGE'
    }
}

export function updateStudentsPage() {
    return {
	    class: class_type,
	    type: 'UPDATE_STUDENTS_PAGE'
    }
}

export function resetStudentsPage() {
    return {
	    class: class_type,
	    type: 'RESET_STUDENTS_PAGE'
    }
}

export function setCurrentTA(ta, index) {
    return {
        class: class_type,
        type: 'SET_CURRENT_TA',
        ta,
        index
    }
}

export const getStudentPreviews = genericDispatch(
    class_type, 'GET_STUDENTS', 'POST'
)

export const getSectionsData = genericDispatch(
    class_type, 'GET_SECTIONS', 'GET'
)

export const getClassProgress = genericDispatch(
    class_type, 'GET_PROGRESS', 'GET'
)

export const getTestBarGraph = genericDispatch(
    class_type, 'GET_TEST_BAR_GRAPH', 'GET',
)

export const setDirectory = genericDispatch(
    class_type, 'SET_DIRECTORY', 'POST'
)

export const syncRepositories = genericDispatch(
	class_type, 'SYNC', 'POST'
)

export const runTests = genericDispatch(
    class_type, 'RUN_TESTS', 'POST'
)

export const getDishonestyReport = genericDispatch(
    class_type, 'GET_DISHONESTY_REPORT', 'GET'
)

export const getSimilarityPlot = genericDispatch(
    class_type, 'GET_SIMILARITY_PLOT', 'GET'
)

export const getClassStatistics = genericDispatch(
    class_type, 'GET_STATISTICS', 'GET'
)

export const submitStudents = genericDispatch(
    class_type, 'SUBMIT_STUDENTS', 'POST'
)

export const getClassCommitHistory = genericDispatch(
    class_type, 'GET_COMMIT_HISTORY', 'GET'
)

export const getClassProgressAnon = genericDispatch(
    class_type, 'GET_PROGRESS_ANON', 'GET'
)

export const getTestBarGraphAnon = genericDispatch(
    class_type, 'GET_TEST_BAR_GRAPH_ANON', 'GET',
)

export const getTeachingAssistants = genericDispatch(
    class_type, 'GET_TEACHING_ASSISTANTS', 'GET'
)