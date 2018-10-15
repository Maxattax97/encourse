import React, { Component } from 'react'
import { connect } from 'react-redux'

import Card from '../Card'
import plusIcon from '../../resources/plus.svg'
import CoursePreview from './util/CoursePreview'
import AccountPreview from './util/AccountPreview'
import Modal from '../modals/Modal'
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
import deleteIcon from '../../resources/delete.svg'

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
            return 'student'
        case 1:
            return 'TA'
        case 2:
            return 'professor'
        case 3:
            return 'admin'
        default:
            return 'student'
        }
    }

    displayCourseOptions = (index) => {
        this.setState({
            show_course_options: true,
            current_course: index,
            modal_blur: ' blur',
            name: this.state.courses[index].name,
            semester: this.state.courses[index].semester
        })
    };

    displayAccountOptions = (index) => {
        this.setState({
            show_account_options: true,
            current_account: index,
            modal_blur: ' blur',
            name: this.props.accounts[index].userName,
            account_type: this.getRole(this.props.accounts[index].role)
        })
    };

    saveCourse = () => {
        //TODO!: verify this works
        if(this.state.current_course === -1) {
            //Add course
            this.props.addCourse(`${url}/api/add/course?courseID=${this.state.name}&semester=${this.state.semester}`,
                {'Authorization': `Bearer ${this.props.token}`})
        } else {
            this.props.modifyCourse(/*!: add endpoint*/)
            //Edit course
        }
    };

    saveAccount = () => {
        //TODO!: verify this works
        if(this.state.current_account === -1) {
            //Add account
            this.props.addAccount(`${url}/api/add/user?userName=${this.state.name}&type=${this.state.account_type}`,
                {'Authorization': `Bearer ${this.props.token}`})
        } else {
            //Edit account
            this.props.modifyAccount(`${url}/api/modify/account?userName=${this.state.name}&field=role&value=${this.state.account_type}`,
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
        this.props.removeAccount(`${url}/api/delete/user?userName=${this.props.account.userName}`,
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
                                    <h2>{ this.state.current_course === -1 ? 'New Course' : `Edit Course ${this.state.name}` }</h2>
                                </div>
                                <h2 className="break-line title" />
                                <h4 className="header">
                                       Course Name
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="name" autoComplete="off"/>
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
                                       Account Name
                                </h4>
                                <input list="student_directory" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="name" autoComplete="off"/>
                                <h4 className="header">
                                       Account Type
                                </h4>
                                <select className="h3-size" value={this.state.account_type} onChange={this.onChange} name="account_type" ref="account_type">
                                    <option value="admin">Admin</option>
                                    <option value="professor">Professor</option>
                                    <option value="student">Student</option>
                                </select>
                                <div className="modal-buttons float-height">
                                    <div onClick={ this.deleteAccount }>
                                        <img src={deleteIcon} />
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