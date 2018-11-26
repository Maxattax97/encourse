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