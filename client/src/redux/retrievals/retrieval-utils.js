import url from '../../server'

export const api_v1 = url + '/api/'

export const projectID_v1 = (project) => 'projectID=' + project.id

export const studentID_v1 = (student) => 'userName=' + student.id

export const page_v1 = (page) => 'page=' + page

export const size_v1 = (size) => 'size=' + size