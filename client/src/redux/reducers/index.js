import { combineReducers } from 'redux'
import { routerReducer } from 'connected-react-router'

import course from './course'
import projects from './projects'
import auth from './auth'

const rootReducer = combineReducers({ auth, course, projects, router: routerReducer })

export default rootReducer