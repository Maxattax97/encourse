import React, { Component } from "react"

import checkmarkIcon from "../../img/checkmark.svg"
import Modal from "./Modal";
import deleteIcon from "../../img/delete.svg";

class CourseModal extends Component {

    constructor(props) {
        super(props);

        this.state = {
            show: false,
            course_directory: "",
            directory_list: ["cs252"],
            interval: 24
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
                               <input list="student_directory" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="student_directory" autoComplete="off"/>
                               <datalist id="student_directory">
                                   {
                                       this.state.directory_list && this.state.directory_list.map &&
                                           this.state.directory_list.map((dir) =>
                                               <option value={dir}/>
                                           )
                                   }
                               </datalist>
                               <h4 className="header">
                                   Repositories Update Interval
                               </h4>
                               <input type="number" className="h3-size" value={this.state.interval} onChange={this.onChange} name="interval" ref="interval"/>
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