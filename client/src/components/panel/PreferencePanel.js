import React, { Component } from 'react'
import { connect } from 'react-redux'

import {
    addCourse,
    addAccount,
    getCourses,
    getAccounts,
    modifyCourse,
    modifyAccount,
    removeCourse, removeAccount, setModalState
} from '../../redux/actions'
import url from '../../server'
import ActionNavigation from '../navigation/ActionNavigation'

import { getCurrentSemesterId } from '../../redux/state-peekers/course'
import ChangePasswordModal from './preference/ChangePasswordModal'
import BackNavigation from '../navigation/BackNavigation'

class PreferencePanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            semester: '',
            account_type: '',
            show_course_options: false,
            show_account_options: false,
            current_course: 0,
            current_account: 0,
            modal_blur: ''
        }
    }

    /*componentDidMount = () => {
        if(this.props.courses.length === 0) {
            this.props.getCourses(`${url}/api/sections`)
        }
        if(this.props.accounts.length === 0) {
            this.props.getAccounts(`${url}/api/accounts`)
        }
    }*/

    resetOptions = () => {
        this.setState(
            {
                name: '',
                semester: this.props.currentSemesterId,
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
            number: this.props.courses[index].courseID,
            semester: this.props.courses[index].semester,
            title: this.props.courses[index].courseTitle,
            crn: this.props.courses[index].crn,
            section_type: this.props.courses[index].sectionType,
            professor: this.props.courses[index].userName,
        })
    }

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
        if(this.state.current_course === -1) {
            //Add course
            this.props.addCourse(`${url}/api/add/section?userName=${this.state.professor}`,
                {
                    'Content-Type': 'application/json'}, JSON.stringify({
                    CRN: this.state.crn,
                    semester: this.state.semester,
                    courseID: this.state.number,
                    courseTitle: this.state.title,
                    sectionType: this.state.section_type,
                }))
        } else {
            //Edit course: Should not be possible
            this.props.modifyCourse(/*TODO: add endpoint*/)
        }
    };

    saveAccount = () => {
        if(this.state.current_account === -1) {
            //Add account
            this.props.addAccount(`${url}/api/add/account`,
                {
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
            this.props.modifyAccount(`${url}/api/modify/account?userName=${this.state.username}`,
                {
                    'Content-Type': 'application/json'}, JSON.stringify({
                    userID: this.state.id,
                    userName: this.state.username,
                    firstName: this.state.first_name,
                    lastName: this.state.last_name,
                    role: this.state.account_type,
                    eduEmail: this.state.email
                }))
        }

    };

    deleteCourse = () => {
        //TODO!: add endpoint
        this.props.removeCourse(`${url}/api/delete/user`)

        this.resetOptions()
    };

    deleteAccount = () => {
        this.props.removeAccount(`${url}/api/delete/user?userName=${this.state.username}`)

        this.resetOptions()
    };

    render() {

        const action_names = [
            'Change Password'
        ]

        const actions = [
            () => this.props.setModalState(1)
        ]

        return (
            <div className="panel-preference">

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <ChangePasswordModal id={1}/>

                {/*<div className="panel-left-nav"/>
                <div className="panel-center-content">
                    <div className={`panel-preference-content${this.state.modal_blur}`}>
                        <div className="title">
                            <h1>Admin Preferences</h1>
                        </div>
                        <h1 className="break-line title" />
                        <div className="panel-course-students float-height">
                            <div className="title float-height" onClick={ () => this.setState({ show_course_options: true, current_course: -1, modal_blur: ' blur' }) }>
                                <PlusIcon/>
                                <h3>Courses</h3>
                            </div>
                            {
                                this.props.courses && this.props.courses.map &&
                                this.props.courses.map((course, index) =>
                                    <Card key={course.sectionIdentifier}
                                        component={<CoursePreview course={course}/>}
                                        onClick={ () => this.displayCourseOptions(index) }/>)
                            }
                        </div>
                        <h2 className="break-line" />
                        <div className="panel-course-students float-height">
                            <div className="title float-height" onClick={ () => this.setState({ show_account_options: true, current_account: -1, modal_blur: ' blur' }) }>
                                <PlusIcon/>
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
                                        <CheckmarkIcon/>
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
                                <input list="student_directory" disabled={this.state.current_account !== -1} className="h3-size" value={this.state.username} onChange={this.onChange} name="username" ref="username" autoComplete="off"/>
                                <h4 className="header">
                                       University ID
                                </h4>
                                <input list="student_directory" disabled={this.state.current_account !== -1} className="h3-size" value={this.state.id} onChange={this.onChange} name="id" ref="id" autoComplete="off"/>
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
                                        <CheckmarkIcon/>
                                    </div>
                                </div>
                            </div>
                        } />
                </div>

                <div className={`modal-overlay${ (this.state.show_course_options || this.state.show_account_options) ? ' show' : '' }`}
                    onClick={ this.resetOptions } />*/}
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentSemesterId: getCurrentSemesterId(state),
        courses: state.admin && state.admin.courses ? state.admin.courses.data.content : [],
        accounts: state.admin && state.admin.accounts ? state.admin.accounts.data.content : [],
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        addCourse: (url, body) => dispatch(addCourse(url, null, body)),
        addAccount: (url, body) => dispatch(addAccount(url, null, body)),
        modifyCourse: (url, body) => dispatch(modifyCourse(url, null, body)),
        modifyAccount: (url, body) => dispatch(modifyAccount(url, null, body)),
        removeCourse: (url, body) => dispatch(removeCourse(url, null, body)),
        removeAccount: (url, body) => dispatch(removeAccount(url, null, body)),
        getCourses: (url, body) => dispatch(getCourses(url, null, body)),
        getAccounts: (url, body) => dispatch(getAccounts(url, null, body)),
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(PreferencePanel)