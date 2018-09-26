import genericDispatch from './fetch'

export function setCurrentProject(project) {
    return {
        type: 'SET_CURRENT_PROJECT',
        project
    }
}