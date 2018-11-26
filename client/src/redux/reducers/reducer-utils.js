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

export function forwardData(state, action, name, dataFunction) {
	return {
		...state,
		...{
			[name]: {
				error: action.hasError,
				loading: !(action.hasError) && !(action.data),
				data: action.data ? dataFunction ? dataFunction(action.data, action.extra, state) : action.data : state[name] ? state[name].data : []
			}
		}
	}
}