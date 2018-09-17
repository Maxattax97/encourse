import React from 'react'
import ReactDOM from 'react-dom'

import { Route } from 'react-router'
import { ConnectedRouter } from 'connected-react-router'
import { Provider } from 'react-redux'

import App from './components/App'
import registerServiceWorker from './registerServiceWorker'
import store, { history } from './redux/store'

ReactDOM.render(
    <Provider store={store}>
        <ConnectedRouter history={history}>
            <Route component={App} />
        </ConnectedRouter>
    </Provider>, 
    document.getElementById('root'))
registerServiceWorker();
