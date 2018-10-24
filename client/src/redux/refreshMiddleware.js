import url from '../server'

export default function refreshMiddleware() {
    return ({ dispatch, getState }) => next => (action) => {
        
        const { request } = action

        if (!request) {
            return next(action)
        }

        const tokens = getState().auth.logInData

        // 5 minutes from now
        const refreshThreshold = Date.now() + 300000

        if(!tokens || !tokens.refresh_token) {
            return request(null, dispatch, true);
        }
        if (tokens && tokens.refresh_token && refreshThreshold > tokens.expires_at) {
            console.log('expiring token')
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
                dispatch({ type: 'LOG_IN_DATA_SUCCESS', data })
                request(data.access_token, dispatch, false)
            })
            .catch((error) => {
                console.log('refresh token error', error)
                //TODO: add error action for refresh
            })
        }
        return request(tokens.access_token, dispatch, false);
    }
}