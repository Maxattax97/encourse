export function genericDispatch(type, actionClass, method) {
	return (url, headers = {}, body, extra) => {
		return {
			type,
            class: actionClass,
			request: (accessToken, dispatch, auth) => {
				if(typeof dispatch !== 'function') return
				dispatch({
                    type,
                    class: actionClass
				})
				const authorization = auth ? `Basic ${btoa('encourse-client:encourse-password')}` : `Bearer ${accessToken}`
				fetch(url, { headers:
						{ 'Authorization': authorization, ...headers }, method, body, mode: 'cors'})
					.then((response) => {
						if (!response.ok || response.status === 204)
							throw Error(response.status + ' ' + response.statusText + ' ')
						return response
					})
					.then((response) => {
						if(response.type !== 'basic') return response.json()
					})
					.then((data) => {
						dispatch({
                            type,
                            class: actionClass,
                            data,
                            extra
                        })
					})
					.catch((error) => {
						console.log(url, '\n', error)
						dispatch({
                            type,
                            class: actionClass,
                            hasError: error,
                            extra
                        })
					})
			}
		}
	}
}

export {
    getStudentPreviews, getClassProgress, getTestBarGraph, setDirectory, 
    getSectionsData, syncRepositories, runTests, getDishonestyReport,
    updateCourseDishonestyPage, resetCourseDishonestyPage, updateStudentsPage,
    resetStudentsPage, getSimilarityPlot, getClassStatistics, submitStudents,
    getClassCommitHistory, getClassProgressAnon, getTestBarGraphAnon,
} from './course' 

export {
    setCurrentProject, toggleHidden,
    getClassProjects, modifyProject, addProject, deleteProject,
    addTest,
} from './projects'

export {
    logIn, logOut, changePassword, getAccount, setLocation
} from './auth'

export {
    setCurrentStudent, clearStudent, updateCommitsPage, resetCommitsPage,
    getStudent, getProgressLine, getCodeFrequency, getCommitFrequency, getStatistics,
    getCommitHistory, getProgressPerTime, getProgressPerCommit,
    syncStudentRepository, runStudentTests
} from './student'

export {
    getCourses, addCourse, modifyCourse, removeCourse, 
    getAccounts, addAccount, modifyAccount, removeAccount,
} from './admin'

export {
    getTeachingAssistants,
} from './teaching_assistant'

export {
    setModalState, toggleSelectAllCards, toggleSelectCard, resetAllCards,
	setFilterState, resetFilterState
} from './control'