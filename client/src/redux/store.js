import { createStore, compose, applyMiddleware } from 'redux'
import refreshMiddleware from './refreshMiddleware'
import createHistory from 'history/createBrowserHistory'
import { routerMiddleware, connectRouter } from 'connected-react-router'
import thunk from 'redux-thunk'
import rootReducer from './reducers/index'

export const history = createHistory()

const router = routerMiddleware(history)
const enhancers = compose(
    applyMiddleware(thunk, router, refreshMiddleware()),
    window.devToolsExtension ? window.devToolsExtension() : f => f
)

const store = createStore(connectRouter(history)(rootReducer), {...getAuthState()}, enhancers)

function setAuthState(state) {
    try {
        localStorage.setItem('state.auth.logInData', JSON.stringify((state.auth || {}).logInData))
    } catch (err) { return undefined }
}

function getAuthState() {
    try {
      const logInData = JSON.parse(localStorage.getItem('state.auth.logInData')) || undefined;

      return { auth: { logInData } }
    } catch (err) { return undefined; }
}
  
store.subscribe(() => {
    setAuthState(store.getState())
})

if(module.hot) {
    module.hot.accept('./reducers/', () => {
        const nextRootReducer = require('./reducers/index').default
        store.replaceReducer(nextRootReducer)
    })
}

export default store