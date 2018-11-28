import {genericDispatch} from "./fetch"

const type = 'TEACHING_ASSISTANT'

export const getTeachingAssistants = genericDispatch(
    type, 'GET_ALL', 'GET'
)