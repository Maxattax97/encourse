import {getData} from "./reducer-utils"

function teachingAssistant(state = {}, action) {
    if(action.class !== 'TEACHING_ASSISTANT')
        return state

    switch(action.type) {
    case 'GET_ALL':
        return getData(state, action, 'getTeachingAssistants')
    default:
	    return Object.assign({}, state, {
		    reduxError: action
	    })
    }
}

export default teachingAssistant