import genericDispatch from './fetch'

export function setCurrentStudent(student) {
    return {
        type: 'SET_CURRENT_STUDENT',
        student
    }
}