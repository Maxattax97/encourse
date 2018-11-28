import {Component} from 'react'
import url from '../../server'
import { defaultCourse } from '../../defaults'
import Modal from './Modal'
import {CheckmarkIcon} from '../Helpers'
import React from 'react'

class CourseDishonestyModal extends Component {

    constructor(props) {
        super(props)

        this.state = {
            show: false,
            course_directory: '',
            interval: 24
        }
    }

    static getDerivedStateFromProps(props, state) {
        return {
            show: props.show,
            course_directory: state.course_directory
        }
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
    };

    saveSettings = () => {
        if(this.state.name === defaultCourse) {
            this.props.setDirectory(`${url}`)
        }
        for(let project of this.props.projects) {
            // console.log(project)
            this.props.modifyProject(`${url}/api/modify/project?projectID=${project.id}&field=testRate&value=${this.state.interval}`)
        }
    };

    render() {
        return (
            <div className="course-modal">
                <Modal center id={this.props.id}>
                    <h2 className='header'>Course Settings</h2>
                    <div className="h2 break-line header"/>
                    <h4 className="header">
                        Student Repositories Directory
                    </h4>
                    <input type="text" className="h3-size" value={this.props.courseID} name="name" autoComplete="off"/>
                    <h4 className="header">
                        Repositories Update Interval
                    </h4>
                    <input type="number" className="h3-size" value={this.state.interval} onChange={this.onChange} name="interval" ref="interval"/>
                    <div className="modal-buttons float-height">
                        <div className="svg-icon action" onClick={ this.saveSettings }>
                            <CheckmarkIcon/>
                        </div>
                    </div>
                </Modal>
            </div>
        )
    }

}

export default CourseDishonestyModal