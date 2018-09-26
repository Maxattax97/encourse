import { combineReducers } from 'redux'
import { routerReducer } from 'connected-react-router'

import course from './course'

const rootReducer = combineReducers({ course, router: routerReducer })

export default rootReducer