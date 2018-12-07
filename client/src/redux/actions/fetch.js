export function genericDispatch(actionClass, type, method) {
	return (url, headers = {}, body, extra) => {
		return {
            type,
            class: actionClass,
			request: (accessToken, dispatch, auth) => {
				if(typeof dispatch !== 'function') return
				if(type === 'GET_STUDENTS') console.log(url)
				dispatch({
                    type,
                    class: actionClass
				})
				const authorization = auth ? `Basic ${btoa('encourse-client:encourse-password')}` : `Bearer ${accessToken}`
				fetch(url, { headers:
						{ 'Authorization': authorization, ...headers }, method, body, mode: 'cors'})
					.then((response) => {
						if (!response.ok || response.status === 204)
							throw Error(response.status + ' ' + response.statusText + ' ')
						return response
					})
					.then((response) => {
						if(response.type !== 'basic') return response.json()
					})
					.then((data) => {
						if(type === 'GET_STUDENTS') console.log(data)
						dispatch({
                            type,
                            class: actionClass,
                            data,
                            extra
                        })
					})
					.catch((error) => {
						console.log(url, '\n', error)
						dispatch({
                            type,
                            class: actionClass,
                            hasError: error,
                            extra
                        })
					})
			}
		}
	}
}
