import {forwardData, getData} from './reducer-utils'

function setTokens(state, action) {
	//const expires_at = Date.now() + (action.data.expires_in) * 1000
	return Object.assign({}, state, {
		authenticateTokenData: action.data,
		authenticateTokenIsLoading: false,
	})
}

//if(action.hasError) -> this is the if statement to determine if error has occurred
//if(action.data) -> this is the if statement to determine if success has occurred
//else (implicit) -> this is the return statement on the start of the fetch request

function logIn(state, action) {
	if(action.hasError)
		return Object.assign({}, state, {
			logInHasError: action.hasError,
			logInIsLoading: false,
		})
	if(action.data)
	    return Object.assign({}, state, {
			logInIsLoading: false,
		})

	return Object.assign({}, state, {
		logInIsLoading: true,
	})
}

function authenticateToken(state, action) {
	if(action.hasError)
	return Object.assign({}, state, {
		authenticateTokenHasError: action.hasError,
		authenticateTokenIsLoading: false,
	})
	if(action.data) {
		console.log(action.data)	
		return setTokens(state, action)
	}
		

	return Object.assign({}, state, {
		authenticateTokenIsLoading: true,
	})
}

function logOut(state, action) {
    if(action.hasError)
	    return Object.assign({}, state, {
		    logOutHasError: action.hasError,
		    logOutIsLoading: false,
	    })
    if(action.data)
	    return Object.assign({}, state, {
		    logInData: null,
		    logOutIsLoading: false,
	    })
    //TODO Jordan Buckmaster: logInData -> null for both the error case and non-error case (success state), are we ok with that?
	return Object.assign({}, state, {
		logInData: null,
		logOutIsLoading: true,
	})
}

function setLocation(state, action) {
	return Object.assign({}, state, {
		prevLocation: action.location,
	})
}

export default function auth(state = {}, action) {
	if(action.class !== 'AUTH')
		return state

	switch(action.type) {
		case 'LOG_IN':
			return logIn(state, action)
		case 'AUTHENTICATE_TOKEN':
			return authenticateToken(state, action)
		case 'SET_TOKENS':
		    return setTokens(state, action)
		case 'LOG_OUT':
		    return logOut(state, action)
		case 'CHANGE_PASSWORD':
		    return getData(state, action, 'changePassword')
		case 'GET_ACCOUNT':
		    return forwardData(state, action, 'getAccount')
		case 'SET_LOCATION':
		    return setLocation(state, action)
		default:
			return Object.assign({}, state, {
				reduxError: action
			})
	}
}