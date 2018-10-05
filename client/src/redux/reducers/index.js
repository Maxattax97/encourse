import { combineReducers } from 'redux'
import { routerReducer } from 'connected-react-router'

import course from './course'
import projects from './projects'
import auth from './auth'
import student from './student'
import admin from './admin'

const rootReducer = combineReducers({ student, auth, course, projects, admin, router: routerReducer })

export default rootReducer