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

export const getStudentPreviews = genericDispatch(
    class_type, 'GET_STUDENTS', 'GET'
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

/*export function getStudentPreviewsDataSuccess(data) {
    if (fuzzing) {
        for (let e of data.content) {
            if (!realToFakeMapping[e.id]) {
                const fake = {}
                fake.id = getFakeUid()
                fake.first_name = faker.name.firstName()
                fake.last_name = faker.name.lastName()
                realToFakeMapping[e.id] = fake

                const real = {}
                real.id = e.id
                real.first_name = e.first_name
                real.last_name = e.last_name
                fakeToRealMapping[fake.id] = real
            }

            e.first_name = realToFakeMapping[e.id].first_name
            e.last_name = realToFakeMapping[e.id].last_name
        }
    }

    return {
        type: 'GET_STUDENT_PREVIEWS_DATA_SUCCESS',
        data
    }
}*/

/*export function getSectionsDataSuccess(data) {
    if (fuzzing) {
        for (let e of data) {
            if (!realToFakeMapping[e.id]) {
                const fake = {}
                fake.id = getFakeUid()
                fake.first_name = faker.name.firstName()
                fake.last_name = faker.name.lastName()
                realToFakeMapping[e.id] = fake

                const real = {}
                real.id = e.id
                real.first_name = e.first_name
                real.last_name = e.last_name
                fakeToRealMapping[fake.id] = real
            }

            e.first_name = realToFakeMapping[e.id].first_name
            e.last_name = realToFakeMapping[e.id].last_name
        }
    }

    return {
        type: 'GET_SECTIONS_DATA_SUCCESS',
        data
    }
}*/