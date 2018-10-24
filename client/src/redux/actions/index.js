export { 
    getStudentPreviews, getClassProgress, getTestBarGraph, setDirectory, 
    getSectionsData, 
} from './course' 

export {
    setCurrentProject, toggleHidden,
    getClassProjects, modifyProject, addProject, deleteProject,
    addTest,
} from './projects'

export {
    logIn, logOut, changePassword,
} from './auth'

export {
    setCurrentStudent, clearStudent,
    getStudent, getProgressLine, getCodeFrequency, getCommitFrequency, getStatistics,
    getCommitHistory, getProgressPerTime, getProgressPerCommit,
} from './student'

export {
    getCourses, addCourse, modifyCourse, removeCourse, 
    getAccounts, addAccount, modifyAccount, removeAccount,
} from './admin'

export {
    getTeachingAssistants
} from './teaching_assistant'