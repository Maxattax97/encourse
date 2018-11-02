export { 
    getStudentPreviews, getClassProgress, getTestBarGraph, setDirectory, 
    getSectionsData, syncRepositories, runTests, getDishonestyReport,
    updateCourseDishonestyPage, resetCourseDishonestyPage, updateStudentsPage,
    resetStudentsPage, getSimilarityPlot, getClassStatistics, submitStudents
} from './course' 

export {
    setCurrentProject, toggleHidden,
    getClassProjects, modifyProject, addProject, deleteProject,
    addTest,
} from './projects'

export {
    logIn, logOut, changePassword, getAccount
} from './auth'

export {
    setCurrentStudent, clearStudent, updateCommitsPage, resetCommitsPage,
    getStudent, getProgressLine, getCodeFrequency, getCommitFrequency, getStatistics,
    getCommitHistory, getProgressPerTime, getProgressPerCommit,
} from './student'

export {
    getCourses, addCourse, modifyCourse, removeCourse, 
    getAccounts, addAccount, modifyAccount, removeAccount,
} from './admin'

export {
    getTeachingAssistants,
} from './teaching_assistant'

export {
    setModalState
} from './modal'