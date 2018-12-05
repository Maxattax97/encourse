import url from '../server'

let refreshing = false
let requests = []

export default function refreshMiddleware() {
    return ({ dispatch, getState }) => next => (action) => {

        const { request, type } = action
        if (!request) {
            return next(action)
        }

        const tokens = getState().auth.logInData
        if(request && type === 'LOG_OUT' && action.class === 'AUTH' ) {
            dispatch({ type, class: 'AUTH' })
        }
        // 5 minutes from now
        const refreshThreshold = Date.now() + 300000
        if(!tokens || !tokens.refresh_token) {
            return request(null, dispatch, true);
        }
        if (!refreshing && tokens && tokens.refresh_token && refreshThreshold > tokens.expires_at) {
            requests.push(request)
            refreshing = true
            let form = new FormData()
            form.append('grant_type', 'refresh_token')
            form.append('refresh_token', tokens.refresh_token)
            form.append('client_id', 'encourse-client')
            return fetch(`${url}/oauth/token`, { headers: {
                'Authorization': `Basic ${btoa('encourse-client:encourse-password')}`,
            }, method: 'POST', body: form, mode: 'cors'})
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
                dispatch({
                    type: 'SET_TOKENS',
                    class: 'AUTH',
                    data })
                refreshing = false
                for(let queuedRequest of requests) {
                    queuedRequest(data.access_token, dispatch, false)
                }
                requests = []
            })
            .catch((error) => {
                console.log('refresh token error', error)
                refreshing = false
                requests = []
                dispatch({ type: 'LOG_OUT', class: 'AUTH' })
            })
        } else if (refreshing) {
            requests.push(request)
        } else {
            return request(tokens.access_token, dispatch, false);
        }
    }
}
