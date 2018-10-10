import genericDispatch from './fetch'
import { fuzzing, realToFakeMapping, fakeToRealMapping, getFakeUid } from '../../fuzz'
import faker from 'faker'

export function getStudentPreviewsHasError(hasError) {
    return {
        type: 'GET_STUDENT_PREVIEWS_HAS_ERROR',
        hasError
    }
}

export function getStudentPreviewsIsLoading(isLoading) {
    return {
        type: 'GET_STUDENT_PREVIEWS_IS_LOADING',
        isLoading
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
    getStudentPreviewsHasError, getStudentPreviewsIsLoading, getStudentPreviewsDataSuccess, 'GET'
)

export function getClassProgressHasError(hasError) {
    return {
        type: 'GET_CLASS_PROGRESS_HAS_ERROR',
        hasError
    }
}

export function getClassProgressIsLoading(isLoading) {
    return {
        type: 'GET_CLASS_PROGRESS_IS_LOADING',
        isLoading
    }
}

export function getClassProgressDataSuccess(data) {
    return {
        type: 'GET_CLASS_PROGRESS_DATA_SUCCESS',
        data
    }
}

export const getClassProgress = genericDispatch(
    getClassProgressHasError, getClassProgressIsLoading, getClassProgressDataSuccess, 'GET'
)

export function getTestBarGraphHasError(hasError) {
    return {
        type: 'GET_TEST_BAR_GRAPH_HAS_ERROR',
        hasError
    }
}

export function getTestBarGraphIsLoading(isLoading) {
    return {
        type: 'GET_TEST_BAR_GRAPH_IS_LOADING',
        isLoading
    }
}

export function getTestBarGraphDataSuccess(data) {
    return {
        type: 'GET_TEST_BAR_GRAPH_DATA_SUCCESS',
        data
    }
}

export const getTestBarGraph = genericDispatch(
    getTestBarGraphHasError, getTestBarGraphIsLoading, getTestBarGraphDataSuccess, 'GET'
)

export function setDirectoryHasError(hasError) {
    return {
        type: 'SET_DIRECTORY_HAS_ERROR',
        hasError
    }
}

export function setDirectoryIsLoading(isLoading) {
    return {
        type: 'SET_DIRECTORY_IS_LOADING',
        isLoading
    }
}

export function setDirectoryDataSuccess(data) {
    return {
        type: 'SET_DIRECTORY_DATA_SUCCESS',
        data
    }
}

export const setDirectory = genericDispatch(
    setDirectoryHasError, setDirectoryIsLoading, setDirectoryDataSuccess, 'POST'
)