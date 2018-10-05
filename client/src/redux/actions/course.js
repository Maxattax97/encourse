import genericDispatch from './fetch'
import { fuzzing, nameMapping } from '../../fuzz'
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
            if (!nameMapping[e.id]) {
                nameMapping[e.id] = {}
                nameMapping[e.id].first_name = faker.name.firstName()
                nameMapping[e.id].last_name = faker.name.lastName()
            }

            e.first_name = nameMapping[e.id].first_name
            e.last_name = nameMapping[e.id].last_name
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
