export { 
    getStudentPreviews, getClassProgress, getTestBarGraph, setDirectory,
} from './course' 

export {
    setCurrentProject, toggleHidden,
    getClassProjects, modifyProject, addProject, deleteProject,
    addTest,
} from './projects'

export {
    logIn, logOut, setToken, logOutClient
} from './auth'

export {
    setCurrentStudent, clearStudent,
    getProgressLine, getCodeFrequency, getCommitFrequency, getStatistics, getCommitHistory
} from './student'

export {
    getCourses, addCourse, modifyCourse, removeCourse, 
    getAccounts, addAccount, modifyAccount, removeAccount,
} from './admin'