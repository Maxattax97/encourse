import { createStore, compose, applyMiddleware } from 'redux'
import createHistory from 'history/createBrowserHistory'
import { routerMiddleware, connectRouter } from 'connected-react-router'
import thunk from 'redux-thunk'
import rootReducer from './reducers/index'

export const history = createHistory()

const router = routerMiddleware(history)
const enhancers = compose(
    applyMiddleware(thunk, router),
    window.devToolsExtension ? window.devToolsExtension() : f => f
)

const store = createStore(connectRouter(history)(rootReducer), {}, enhancers)

if(module.hot) {
    module.hot.accept('./reducers/', () => {
        const nextRootReducer = require('./reducers/index').default
        store.replaceReducer(nextRootReducer)
    })
}

export default store