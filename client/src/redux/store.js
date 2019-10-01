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
        projects: {
            ...getLocalStorageState('currentProjectId'),
            ...getLocalStorageState('currentProjectIndex'),
        },
        auth: {
            ...getLocalStorageState('authenticateTokenData'),
            ...getLocalStorageState('getAccountData'),
        },
        student: {
            ...getLocalStorageState('student'),
        },  
        course: {
            ...getLocalStorageState('currentCourseId'),
            ...getLocalStorageState('currentSemesterId'),
        }  
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
        return { [key]: value } 
    } catch (err) {
        return undefined
    }
}
  
store.subscribe(() => {
    let state = store.getState()
    setLocalStorageState(state, 'authenticateTokenData', JSON.stringify((state.auth || {}).authenticateTokenData))
    setLocalStorageState(state, 'getAccountData', JSON.stringify((state.auth || {}).getAccountData))
    setLocalStorageState(state, 'currentProjectId', JSON.stringify((state.projects || {}).currentProjectId))
    setLocalStorageState(state, 'currentProjectIndex', JSON.stringify((state.projects || {}).currentProjectIndex))

    if(state.student && state.student.student)
        setLocalStorageState(state, 'student', JSON.stringify(state.student.student || {}))

    setLocalStorageState(state, 'currentCourseId', JSON.stringify((state.course || {}).currentCourseId))
    setLocalStorageState(state, 'currentSemesterId', JSON.stringify((state.course || {}).currentSemesterId))
})

if(module.hot) {
    module.hot.accept('./reducers/', () => {
        const nextRootReducer = require('./reducers/index').default
        store.replaceReducer(nextRootReducer)
    })
}

export default store