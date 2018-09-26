export { 
    getStudentPreviewsHasError, getStudentPreviewsIsLoading, getStudentPreviewsDataSuccess, getStudentPreviews,
    getClassProjectsHasError, getClassProjectsIsLoading, getClassProjectsDataSuccess, getClassProjects 
} from './course' 

export {
    setCurrentProject
} from './projects'

export {
    logInHasError, logInIsLoading, logInDataSuccess, logIn,
    logOutHasError, logOutIsLoading, logOutDataSuccess, logOut,
    setToken, logOutClient
} from './auth'