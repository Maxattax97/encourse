const createPeeker = (name) => (state) => state.admin && state.admin[name] ? state.admin[name] : { loading: true, data: [] }

export const getAllCourses = createPeeker('courses')

export const getAddCourse = createPeeker('addCourse')

export const getModifyCourse = createPeeker('modifyCourse')

export const getRemoveCourse = createPeeker('removeCourse')

export const getAllAccounts = createPeeker('accounts')

export const getAddAccount = createPeeker('addAccount')

export const getModifyAcccount = createPeeker('modifyAccount')

export const getRemoveAccount = createPeeker('removeAccount')