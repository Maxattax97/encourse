import {genericDispatch} from "./index"

const type = 'TEACHING_ASSISTANT'

export const getTeachingAssistants = genericDispatch(
    type, 'GET_TEACHING_ASSISTANTS', 'GET'
)