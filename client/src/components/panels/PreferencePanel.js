import React, { Component } from 'react'
import Card from "../Card";
import plusIcon from "../../img/plus.svg";
import StudentPreview from "./util/StudentPreview";
import CoursePreview from "./util/CoursePreview";
import AccountPreview from "./util/AccountPreview";
import ProjectModal from "../modals/ProjectModal";
import Modal from "../modals/Modal";
import checkmarkIcon from "../../img/checkmark.svg";


class PreferencePanel extends Component {

    constructor(props) {
        super(props);

        this.state = {
            courses: [{
                name: "CS252",
                semester: "Fall 2018",
                professor: "Gustavo",
                id: "1"
            }],
            accounts: [{
                login: "kleclain",
                account_type: "admin",
                course_professor: [],
                course_ta: [],
                id: "1"
            }],
            name: "",
            semester: "",
            account_type: "",
            show_course_options: false,
            show_account_options: false,
            current_course: 0,
            current_account: 0,
            modal_blur: ""
        }
    }

    resetOptions = () => {
        this.setState(
            {
                name: "",
                semester: "Fall 2018",
                account_type: "student",
                show_course_options: false,
                show_account_options: false,
                current_course: 0,
                current_account: 0,
                modal_blur: ""
            })
    };

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value });
    };

    displayCourseOptions = (index) => {
        this.setState({
            show_course_options: true,
            current_course: index,
            modal_blur: " blur",
            name: this.state.courses[index].name,
            semester: this.state.courses[index].semester
        });
    };

    displayAccountOptions = (index) => {
        this.setState({
            show_account_options: true,
            current_account: index,
            modal_blur: " blur",
            name: this.state.accounts[index].name,
            account_type: this.state.accounts[index].account_type
        });
    };

    saveCourse = () => {
        //TODO Bucky
    };

    saveAccount = () => {
        //TODO Bucky
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
                            <div className="title float-height" onClick={ () => this.setState({ show_course_options: true, current_course: -1, modal_blur: " blur" }) }>
                                <img src={ plusIcon } />
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
                            <div className="title float-height" onClick={ () => this.setState({ show_account_options: true, current_account: -1, modal_blur: " blur" }) }>
                                <img src={ plusIcon } />
                                <h3>Accounts</h3>
                            </div>
                            {
                                this.state.accounts && this.state.accounts.map &&
                                this.state.accounts.map((account, index) =>
                                    <Card key={account.id}
                                          component={<AccountPreview account={account}/>}
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
                                       <h2>{ this.state.current_course === -1 ? "New Course" : `Edit Course ${this.state.name}` }</h2>
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
                                       <option value="Fall 2018">Fall 2018</option>
                                       <option value="Spring 2019">Spring 2019</option>
                                   </select>
                                   <div className="modal-buttons float-height">
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
                                       <h2>{ this.state.current_account === -1 ? "New Account" : `Edit Account ${this.state.name}` }</h2>
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
                                       <div className="project-options-add" onClick={ this.saveAccount }>
                                           <img src={ checkmarkIcon } />
                                       </div>
                                   </div>
                               </div>
                           } />
                </div>

                <div className={`modal-overlay${ (this.state.show_course_options || this.state.show_account_options) ? " show" : "" }`}
                     onClick={ this.resetOptions } />
            </div>
        )
    }
}

export default PreferencePanel