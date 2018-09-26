import { combineReducers } from 'redux'
import { routerReducer } from 'connected-react-router'

import course from './course'
import projects from './projects'
import auth from './auth'
import student from './student'

const rootReducer = combineReducers({ student, auth, course, projects, router: routerReducer })

export default rootReducer