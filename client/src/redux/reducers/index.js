import { combineReducers } from 'redux'
import { routerReducer } from 'connected-react-router'

import course from './course'
import projects from './projects'

const rootReducer = combineReducers({ course, projects, router: routerReducer })

export default rootReducer