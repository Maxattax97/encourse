/* eslint no-console: off */

export default function genericDispatch(type, hasError, success, method) {
    return function specificDispatch(url, headers = {}, body, extra) {
        return {
            type,
            request: (accessToken, dispatch, auth) => {
                    if(typeof dispatch !== 'function') return
                    dispatch({ type })
                    const authorization = auth ? `Basic ${btoa('encourse-client:encourse-password')}` : `Bearer ${accessToken}` 
                    fetch(url, { headers:  
                        { 'Authorization': authorization,
                          ...headers }, method, body, mode: 'cors'})
                    .then((response) => {
                        if (!response.ok || response.status === 204) {
                            throw Error(response.status + ' ' + response.statusText)
                        }
                        return response
                    })
                    .then((response) => {
                        const contentType = response.headers.get("content-type");
                        if (contentType && contentType.indexOf("application/json") !== -1) {
                          return response.json()
                        } else {
                          return response.text()
                        }
                    })
                    .then((data) => {
                        dispatch(success(data, extra))
                    })
                    .catch((error) => {
                        console.log(url, '\n', error)
                        dispatch(hasError(true))
                    })
                }
        }
    }
}