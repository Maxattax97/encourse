export { 
    getStudentPreviews, getClassProgress, getTestBarGraph, setDirectory, 
    getSectionsData, syncRepositories, runTests, getDishonestyReport,
    updateCourseDishonestyPage, resetCourseDishonestyPage, updateStudentsPage,
    resetStudentsPage, getSimilarityPlot, getClassStatistics
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
    setCurrentStudent, clearStudent, updateCommitsPage, resetCommitsPage,
    getStudent, getProgressLine, getCodeFrequency, getCommitFrequency, getStatistics,
    getCommitHistory, getProgressPerTime, getProgressPerCommit,
} from './student'

export {
    getCourses, addCourse, modifyCourse, removeCourse, 
    getAccounts, addAccount, modifyAccount, removeAccount,
} from './admin'

export {
    getTeachingAssistants, submitStudents,
} from './teaching_assistant'

export {
    setModalState
} from './modal'