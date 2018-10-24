import genericDispatch from './fetch'
import { fuzzing, realToFakeMapping, fakeToRealMapping, getFakeUid } from '../../fuzz'
import faker from 'faker'

export function getStudentPreviewsHasError(hasError) {
    return {
        type: 'GET_STUDENT_PREVIEWS_HAS_ERROR',
        hasError
    }
}

export function getStudentPreviewsDataSuccess(data) {
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
        type: 'GET_STUDENT_PREVIEWS_DATA_SUCCESS',
        data
    }
}

export const getStudentPreviews = genericDispatch(
   'GET_STUDENT_PREVIEWS', getStudentPreviewsHasError, getStudentPreviewsDataSuccess, 'GET'
)

export function getSectionsDataError(hasError) {
    return {
        type: 'GET_SECTIONS_DATA_HAS_ERROR',
        hasError
    }
}

export function getSectionsDataSuccess(data) {
    /*if (fuzzing) {
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
    }*/

    return {
        type: 'GET_SECTIONS_DATA_SUCCESS',
        data
    }
}

export const getSectionsData = genericDispatch(
    'GET_SECTIONS_DATA', getSectionsDataError, getSectionsDataSuccess, 'GET'
)

export function getClassProgressHasError(hasError) {
    return {
        type: 'GET_CLASS_PROGRESS_HAS_ERROR',
        hasError
    }
}

export function getClassProgressDataSuccess(data) {
    return {
        type: 'GET_CLASS_PROGRESS_DATA_SUCCESS',
        data
    }
}

export const getClassProgress = genericDispatch(
    'GET_CLASS_PROGRESS', getClassProgressHasError, getClassProgressDataSuccess, 'GET'
)

export function getTestBarGraphHasError(hasError) {
    return {
        type: 'GET_TEST_BAR_GRAPH_HAS_ERROR',
        hasError
    }
}

export function getTestBarGraphDataSuccess(data) {
    return {
        type: 'GET_TEST_BAR_GRAPH_DATA_SUCCESS',
        data
    }
}

export const getTestBarGraph = genericDispatch(
    'GET_TEST_BAR_GRAPH', getTestBarGraphHasError, getTestBarGraphDataSuccess, 'GET',
)

export function setDirectoryHasError(hasError) {
    return {
        type: 'SET_DIRECTORY_HAS_ERROR',
        hasError
    }
}

export function setDirectoryDataSuccess(data) {
    return {
        type: 'SET_DIRECTORY_DATA_SUCCESS',
        data
    }
}

export const setDirectory = genericDispatch(
    'SET_DIRECTORY', setDirectoryHasError, setDirectoryDataSuccess, 'POST'
)