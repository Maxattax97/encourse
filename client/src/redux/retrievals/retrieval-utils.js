import url from '../../server'

export const api_v1 = url + '/api/'

export const projectID_v1 = project => 'projectID=' + project.id

export const studentID_v1 = student => 'userName=' + student.id

export const courseID_v1 = id => 'courseID=' + id

export const semester_v1 = semester => 'semester=' + semester

export const page_v1 = page => 'page=' + page

export const size_v1 = size => 'size=' + size

export const anon_v1 = 'anonymous=true'