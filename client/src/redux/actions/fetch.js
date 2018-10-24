/* eslint no-console: off */

export default function genericDispatch(type, hasError, success, method) {
    return function specificDispatch(url, headers, body, extra) {
        return {
            type,
            request: (tokens, dispatch) => {
                    if(typeof dispatch !== 'function') return
                    fetch(url, { headers, method, body, mode: 'cors'})
                    .then((response) => {
                        if (!response.ok) {
                            throw Error(response.status + ' ' + response.statusText)
                        }
                        return response
                    })
                    .then((response) => {
                        return response.json()
                    })
                    .then((data) => {
                        dispatch(success(data, extra))
                    })
                    .catch((error) => {
                        console.log(error)
                        dispatch(hasError(true))
                    })
                }
        }
    }
}