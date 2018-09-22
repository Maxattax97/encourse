import { combineReducers } from 'redux'
import { routerReducer } from 'connected-react-router'

const rootReducer = combineReducers({ router: routerReducer })

export default rootReducer