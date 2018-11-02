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

const store = createStore(connectRouter(history)(rootReducer), 
    {
        ...getLocalStorageState('logInData', 'auth'),
        ...getLocalStorageState('getAccountData', 'auth'),
        ...getLocalStorageState('currentProjectId', 'projects'),
        ...getLocalStorageState('currentProjectIndex', 'projects'),
        ...getLocalStorageState('currentStudent', 'student'),
    }, 
    enhancers)

function setLocalStorageState(state, key, value) {
    try {
        localStorage.setItem(key, value)
    } catch (err) {
        return undefined
    }
}

function getLocalStorageState(key, parentObject) {
    try {
        const value = JSON.parse(localStorage.getItem(key)) || undefined
        return { [parentObject]: { [key]: value } }
    } catch (err) {
        return undefined
    }
}
  
store.subscribe(() => {
    let state = store.getState()
    setLocalStorageState(state, 'logInData', JSON.stringify((state.auth || {}).logInData))
    setLocalStorageState(state, 'getAccountData', JSON.stringify((state.auth || {}).getAccountData))
    setLocalStorageState(state, 'currentProjectId', JSON.stringify((state.projects || {}).currentProjectId))
    setLocalStorageState(state, 'currentProjectIndex', JSON.stringify((state.projects || {}).currentProjectIndex))
    setLocalStorageState(state, 'currentStudent', JSON.stringify((state.student || {}).currentStudent))
})

if(module.hot) {
    module.hot.accept('./reducers/', () => {
        const nextRootReducer = require('./reducers/index').default
        store.replaceReducer(nextRootReducer)
    })
}

export default store