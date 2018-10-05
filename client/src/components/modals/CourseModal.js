import React, { Component } from "react"

import checkmarkIcon from "../../img/checkmark.svg"
import Modal from "./Modal";
import deleteIcon from "../../img/delete.svg";

class CourseModal extends Component {

    constructor(props) {
        super(props);

        this.state = {
            show: false,
            course_directory: ""
        };
    }

    static getDerivedStateFromProps(props, state) {
        return {
            show: props.show,
            course_directory: state.course_directory
        };
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value });
    };

    saveSettings = () => {
        //TODO Bucky save course data here
    };

    render() {

        const modal_buttons =
            <div className="modal-buttons float-height">
                <div className="project-options-add" onClick={ this.saveSettings }>
                    <img src={ checkmarkIcon } />
                </div>
            </div>;

        return (
            <div className="course-options">
                <Modal center
                       show={ this.props.show }
                       onExit={ this.props.close }
                       component={
                           <div className="panel-course-options">
                               <div className="title">
                                   <h2>Course Settings</h2>
                               </div>
                               <h2 className="break-line title" />
                               <h4 className="header">
                                   Student Repositories Directory
                               </h4>
                               <input type="text" className="h3-size" value={this.state.name} onChange={this.onChange} name="student_directory" ref="student_directory"/>
                               { modal_buttons }
                           </div>
                       } />


                <div className={`modal-overlay${ this.props.show ? " show" : "" }`}
                     onClick={ this.props.close } />
            </div>
        );
    }

}

export default CourseModal;