export function getData(state, action, name) {
	if(action.hasError)
		return Object.assign({}, state, {
			[name + 'HasError']: action.hasError,
			[name + 'IsLoading']: false,
		})
	if(action.data)
		return Object.assign({}, state, {
			[name + 'Data']: action.data,
			[name + 'IsLoading']: false,
		})
	return Object.assign({}, state, {
		[name + 'IsLoading']: true,
	})
}

export function unknownAction(state, action) {
    return {
        ...state,
        reduxError: action
    }
}

export function resetData(type) {
	return {
		[type]: {
			loading: false,
			hasError: null,
			data: null
		}
	}
}

export function forwardData(state, action, name, dataFunction, pagination) {
    let data = (action.data ? dataFunction ? dataFunction(pagination ? action.data.content : action.data, action.extra, state) : pagination ? action.data.content : action.data : state[name] ? state[name].data : [])

	return {
		...state,
		...{
			[name]: {
				error: action.hasError,
				loading: !(action.hasError) && !(action.data),
                page: pagination && action.data ? {...action.data, content: null} : null,
				data: data
			}
		}
	}
}