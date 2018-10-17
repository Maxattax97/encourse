import React, { Component } from 'react'
import { connect } from 'react-redux'

import { Card } from '../Helpers'
import plusIcon from '../../resources/plus.svg'
import CoursePreview from './util/CoursePreview'
import AccountPreview from './util/AccountPreview'
import Modal from '../modal/Modal'
import checkmarkIcon from '../../resources/checkmark.svg'
import {
    addCourse,
    addAccount,
    getCourses,
    getAccounts,
    modifyCourse,
    modifyAccount,
    removeCourse, removeAccount
} from '../../redux/actions'
import url from '../../server'
import deleteIcon from '../../resources/trash.svg'

class PreferencePanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            courses: [{
                name: 'CS252',
                semester: 'Fall2018',
                professor: 'Gustavo',
                id: '1'
            }],
            accounts: [{
                login: 'kleclain',
                account_type: 'admin',
                course_professor: [],
                course_ta: [],
                id: '1'
            }],
            name: '',
            semester: '',
            account_type: '',
            show_course_options: false,
            show_account_options: false,
            current_course: 0,
            current_account: 0,
            modal_blur: ''
        }
    }

    componentDidMount = () => {
        if(this.props.courses.length === 0) {
            this.props.getCourses(/*TODO!: add endpoint*/'',
                {'Authorization': `Bearer ${this.props.token}`})
        }
        if(this.props.accounts.length === 0) {
            this.props.getAccounts(`${url}/api/accounts`,
                {'Authorization': `Bearer ${this.props.token}`})
        }
    }

    resetOptions = () => {
        this.setState(
            {
                name: '',
                semester: 'Fall2018',
                account_type: 'student',
                show_course_options: false,
                show_account_options: false,
                current_course: 0,
                current_account: 0,
                modal_blur: ''
            })
    };

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
    };

    getRole = (role) => {
        switch(role) {
        case 0:
            return 'STUDENT'
        case 1:
            return 'TA'
        case 2:
            return 'PROFESSOR'
        case 3:
            return 'ADMIN'
        default:
            return 'STUDENT'
        }
    }

    displayCourseOptions = (index) => {
        this.setState({
            show_course_options: true,
            current_course: index,
            modal_blur: ' blur',
            number: this.state.courses[index].courseID,
            semester: this.state.courses[index].semester
        })
    };

    displayAccountOptions = (index) => {
        this.setState({
            show_account_options: true,
            current_account: index,
            modal_blur: ' blur',
            first_name: this.props.accounts[index].firstName,
            last_name: this.props.accounts[index].lastName,
            id: this.props.accounts[index].userID,
            email: this.props.accounts[index].eduEmail,
            username: this.props.accounts[index].userName,
            account_type: this.getRole(this.props.accounts[index].role)
        })
    };

    getRoleInt = (role) => {
        switch(role) {
        case 'STUDENT':
            return 0
        case 'TA':
            return 1
        case 'PROFESSOR':
            return 2
        case 'ADMIN':
            return 3
        default:
            return 0
        }
    }

    saveCourse = () => {
        //TODO!: verify this works
        if(this.state.current_course === -1) {
            //Add course
            this.props.addCourse(`${url}/api/add/section?userName=${this.state.professor}`,
                {'Authorization': `Bearer ${this.props.token}`,
                'Content-Type': 'application/json'}, JSON.stringify({
                    CRN: this.state.crn,
                    semester: this.state.semester,
                    courseID: this.state.number,
                    courseTitle: this.state.title,
                    sectionType: this.state.section_type,
                }))
        } else {
            this.props.modifyCourse(/*TODO: add endpoint*/)
            //Edit course
        }
    };

    saveAccount = () => {
        //TODO!: verify this works
        if(this.state.current_account === -1) {
            //Add account
            this.props.addAccount(`${url}/api/add/account`,
                {'Authorization': `Bearer ${this.props.token}`,
                'Content-Type': 'application/json'}, JSON.stringify({
                    userID: this.state.id,
                    userName: this.state.username,
                    firstName: this.state.first_name,
                    lastName: this.state.last_name,
                    role: this.getRoleInt(this.state.account_type),
                    eduEmail: this.state.email
                }))
        } else {
            //Edit account
            this.props.modifyAccount(`${url}/api/modify/authority?userName=${this.state.username}&role=${this.state.account_type}`,
                {'Authorization': `Bearer ${this.props.token}`})
        }

    };

    deleteCourse = () => {
        //TODO!: add endpoint
        this.props.removeCourse(`${url}/api/delete/user`,
            {'Authorization': `Bearer ${this.props.token}`})

        this.resetOptions()
    };

    deleteAccount = () => {
        //TODO!: add parameter to delete any account
        this.props.removeAccount(`${url}/api/delete/user?userName=${this.state.name}`,
            {'Authorization': `Bearer ${this.props.token}`})

        this.resetOptions()
    };

    render() {
        return (
            <div className="panel-preference">
                <div className="panel-left-nav"/>
                <div className="panel-center-content">
                    <div className={`panel-preference-content${this.state.modal_blur}`}>
                        <div className="title">
                            <h1>Admin Preferences</h1>
                        </div>
                        <h1 className="break-line title" />
                        <div className="panel-course-students float-height">
                            <div className="title float-height" onClick={ () => this.setState({ show_course_options: true, current_course: -1, modal_blur: ' blur' }) }>
                                <img src={ plusIcon } alt="Add Course"/>
                                <h3>Courses</h3>
                            </div>
                            {
                                this.state.courses && this.state.courses.map &&
                                this.state.courses.map((course, index) =>
                                    <Card key={course.id}
                                        component={<CoursePreview course={course}/>}
                                        onClick={ () => this.displayCourseOptions(index) }/>)
                            }
                        </div>
                        <h2 className="break-line" />
                        <div className="panel-course-students float-height">
                            <div className="title float-height" onClick={ () => this.setState({ show_account_options: true, current_account: -1, modal_blur: ' blur' }) }>
                                <img src={ plusIcon } alt="Add Account"/>
                                <h3>Accounts</h3>
                            </div>
                            {
                                this.props.accounts && this.props.accounts.map &&
                                this.props.accounts.map((account, index) =>
                                    <Card key={account.userName}
                                        component={<AccountPreview getRole={this.getRole} account={account}/>}
                                        onClick={ () => this.displayAccountOptions(index) }/>)
                            }
                        </div>
                    </div>
                </div>

                <div className="course-options">
                    <Modal center
                        show={ this.state.show_course_options }
                        onExit={ this.resetOptions }
                        component={
                            <div className="panel-course-options">
                                <div className="title">
                                    <h2>{ this.state.current_course === -1 ? 'New Course' : `Edit Course ${this.state.number}` }</h2>
                                </div>
                                <h2 className="break-line title" />
                                <h4 className="header">
                                       Course Number
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.number} onChange={this.onChange} name="number" ref="number" autoComplete="off"/>
                                <h4 className="header">
                                       Course Title
                                </h4>    
                                <input list="student_directory" className="h3-size" value={this.state.title} onChange={this.onChange} name="title" ref="title" autoComplete="off"/>
                                <h4 className="header">
                                       Professor Username
                                </h4>                             
                                <input list="student_directory" className="h3-size" value={this.state.professor} onChange={this.onChange} name="professor" ref="professor" autoComplete="off"/>
                                <h4 className="header">
                                       CRN
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.crn} onChange={this.onChange} name="crn" ref="crn" autoComplete="off"/>
                                <h4 className="header">
                                       Section Type
                                </h4>       
                                <input list="student_directory" className="h3-size" value={this.state.section_type} onChange={this.onChange} name="section_type" ref="section_type" autoComplete="off"/>
                                <h4 className="header">
                                       Semester
                                </h4>
                                <select className="h3-size" value={this.state.semester} onChange={this.onChange} name="semester" ref="semester">
                                    <option value="Fall2018">Fall 2018</option>
                                    <option value="Spring2019">Spring 2019</option>
                                </select>
                                <div className="modal-buttons float-height">
                                    <div onClick={ this.deleteCourse }>
                                        <img src={deleteIcon} />
                                    </div>

                                    <div className="project-options-add" onClick={ this.saveCourse }>
                                        <img src={ checkmarkIcon } />
                                    </div>
                                </div>
                            </div>
                        } />
                </div>

                <div className="course-options">
                    <Modal center
                        show={ this.state.show_account_options }
                        onExit={ this.resetOptions }
                        component={
                            <div className="panel-course-options">
                                <div className="title">
                                    <h2>{ this.state.current_account === -1 ? 'New Account' : `Edit Account ${this.state.name}` }</h2>
                                </div>
                                <h2 className="break-line title" />
                                <h4 className="header">
                                       First Name
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.first_name} onChange={this.onChange} name="first_name" ref="first_name" autoComplete="off"/>
                                <h4 className="header">
                                       Last Name
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.last_name} onChange={this.onChange} name="last_name" ref="last_name" autoComplete="off"/>
                                <h4 className="header">
                                       Username
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.username} onChange={this.onChange} name="username" ref="username" autoComplete="off"/>
                                <h4 className="header">
                                       University ID
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.id} onChange={this.onChange} name="id" ref="id" autoComplete="off"/>
                                <h4 className="header">
                                       University E-mail
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.email} onChange={this.onChange} name="email" ref="email" autoComplete="off"/>
                                <h4 className="header">
                                       Account Type
                                </h4>
                                <select className="h3-size" value={this.state.account_type} onChange={this.onChange} name="account_type" ref="account_type">
                                    <option value="ADMIN">Admin</option>
                                    <option value="PROFESSOR">Professor</option>
                                    <option value="TA">TA</option>
                                    <option value="STUDENT">Student</option>
                                </select>
                                <div className="modal-buttons float-height">
                                    <div onClick={ this.deleteAccount }>
                                        <img src={ deleteIcon } />
                                    </div>

                                    <div className="project-options-add" onClick={ this.saveAccount }>
                                        <img src={ checkmarkIcon } />
                                    </div>
                                </div>
                            </div>
                        } />
                </div>

                <div className={`modal-overlay${ (this.state.show_course_options || this.state.show_account_options) ? ' show' : '' }`}
                    onClick={ this.resetOptions } />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        courses: state.admin && state.admin.getCoursesData ? state.admin.getCoursesData : [],
        accounts: state.admin && state.admin.getAccountsData ? state.admin.getAccountsData : [],
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        addCourse: (url, headers, body) => dispatch(addCourse(url, headers, body)),
        addAccount: (url, headers, body) => dispatch(addAccount(url, headers, body)),
        modifyCourse: (url, headers, body) => dispatch(modifyCourse(url, headers, body)),
        modifyAccount: (url, headers, body) => dispatch(modifyAccount(url, headers, body)),
        removeCourse: (url, headers, body) => dispatch(removeCourse(url, headers, body)),
        removeAccount: (url, headers, body) => dispatch(removeAccount(url, headers, body)),
        getCourses: (url, headers, body) => dispatch(getCourses(url, headers, body)),
        getAccounts: (url, headers, body) => dispatch(getAccounts(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(PreferencePanel)