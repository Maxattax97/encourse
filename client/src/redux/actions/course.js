import {genericDispatch} from "./index"

const type = 'COURSE'

export function updateCourseDishonestyPage() {
    return {
        type,
        class: 'UPDATE_COURSE_DISHONESTY_PAGE'
    }
}

export function resetCourseDishonestyPage() {
    return {
	    type,
	    class: 'RESET_COURSE_DISHONESTY_PAGE'
    }
}

export function updateStudentsPage() {
    return {
	    type,
	    class: 'UPDATE_STUDENTS_PAGE'
    }
}

export function resetStudentsPage() {
    return {
	    type,
	    class: 'RESET_STUDENTS_PAGE'
    }
}

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

export const getStudentPreviews = genericDispatch(
    type, 'GET_STUDENT_PREVIEWS', 'GET'
)

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

export const getSectionsData = genericDispatch(
    type, 'GET_SECTIONS_DATA', 'GET'
)

export const getClassProgress = genericDispatch(
    type, 'GET_CLASS_PROGRESS', 'GET'
)

export const getTestBarGraph = genericDispatch(
    type, 'GET_TEST_BAR_GRAPH', 'GET',
)

export const setDirectory = genericDispatch(
    type, 'SET_DIRECTORY', 'POST'
)

export const syncRepositories = genericDispatch(
	type, 'SYNC_REPOSITORIES', 'POST'
)

export const runTests = genericDispatch(
    type, 'RUN_TESTS', 'POST'
)

export const getDishonestyReport = genericDispatch(
    type, 'GET_DISHONESTY_REPORT', 'GET'
)

export const getSimilarityPlot = genericDispatch(
    type, 'GET_SIMILARITY_PLOT', 'GET'
)

export const getClassStatistics = genericDispatch(
    type, 'GET_CLASS_STATISTICS', 'GET'
)

export const submitStudents = genericDispatch(
    type, 'SUBMIT_STUDENTS', 'POST'
)

export const getClassCommitHistory = genericDispatch(
    type, 'GET_CLASS_COMMIT_HISTORY', 'GET'
)

export const getClassProgressAnon = genericDispatch(
    type, 'GET_CLASS_PROGRESS_ANON', 'GET'
)

export const getTestBarGraphAnon = genericDispatch(
    type, 'GET_TEST_BAR_GRAPH_ANON', 'GET',
)