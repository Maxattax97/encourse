import {getData} from "./reducer-utils"

function teachingAssistant(state = {}, action) {
    if(action.type !== 'TEACHING_ASSISTANT')
        return state

    switch(action.class) {
    case 'GET_TEACHING_ASSISTANTS':
        return getData(state, action, 'getTeachingAssistants')
    default:
	    return Object.assign({}, state, {
		    reduxError: action
	    })
    }
}

export default teachingAssistant