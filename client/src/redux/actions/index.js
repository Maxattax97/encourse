export {
    setCurrentCourse, setCurrentSemester,
    getStudentPreviews, getClassProgress, getTestBarGraph, setDirectory, 
    getSectionsData, syncRepositories, runTests, getDishonestyReport,
    updateCourseDishonestyPage, resetCourseDishonestyPage, updateStudentsPage,
    resetStudentsPage, getSimilarityPlot, getClassStatistics, submitStudents,
    getClassCommitHistory, getClassProgressAnon, getTestBarGraphAnon, getTeachingAssistants
} from './course' 

export {
    setCurrentProject, toggleHidden,
    getClassProjects, modifyProject, addProject, deleteProject,
    addTest, runTestSuite
} from './projects'

export {
    logIn, logOut, changePassword, getAccount, setLocation
} from './auth'

export {
    setCurrentStudent, clearStudent, updateCommitsPage, resetCommitsPage,
    getStudent, getProgressLine, getCodeFrequency, getCommitFrequency, getStatistics,
    getCommitHistory, getProgressPerTime, getProgressPerCommit,
    syncStudentRepository, runStudentTests, getSource
} from './student'

export {
    getCourses, addCourse, modifyCourse, removeCourse, 
    getAccounts, addAccount, modifyAccount, removeAccount,
} from './admin'

export {
    setModalState, toggleSelectAllCards, toggleSelectCard, resetAllCards,
	setFilterState, resetFilterState
} from './control'