/* eslint no-console: off */

export default function genericDispatch(hasError, isLoading, success, method) {
    function specificDispatch(url, headers, body, extra) {
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method, body, mode: 'cors'})
                .then((response) => {
                    if (!response.ok) {
                        throw Error(response.status + ' ' + response.statusText)
                    }
                    console.log(response)
                    dispatch(isLoading(false))
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
    return specificDispatch
}