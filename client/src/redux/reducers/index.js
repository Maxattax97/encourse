import { combineReducers } from 'redux'

import course from './course'
import projects from './projects'
import auth from './auth'
import student from './student'
import admin from './admin'
import teachingAssistant from './teaching_assistant'
import modal from './modal'

const rootReducer = combineReducers({ student, auth, course, projects, admin, teachingAssistant, modal })

export default rootReducer