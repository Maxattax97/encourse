import React, { Component } from 'react'
import { connect } from 'react-redux'

import checkmarkIcon from '../../resources/checkmark.svg'
import Modal from './Modal'
// import deleteIcon from '../../img/trash.svg'
import { setDirectory, modifyProject } from '../../redux/actions'
import url from '../../server'
import {Title} from '../Helpers'

class CourseModal extends Component {

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
        if(this.state.name === 'cs252') {
            this.props.setDirectory(`${url}`)
        }
        for(let project of this.props.projects) {
            // console.log(project)
            this.props.modifyProject(`${url}/api/modify/project?projectID=${project.id}&field=testRate&value=${this.state.interval}`,
                {'Authorization': `Bearer ${this.props.token}`})
        }    
    };

    render() {
        const modal_buttons =
            <div className="modal-buttons float-height">
                <div className="project-options-add" onClick={ this.saveSettings }>
                    <img src={ checkmarkIcon } />
                </div>
            </div>

        return (
            <div className="course-modal">
                <Modal center
                    show={ this.props.show }
                    onExit={ this.props.close }
                    component={
                        <div className="modal-container">
                            <Title header={<h2 className='header'>Course Settings</h2> }/>
                            <div className="h2 break-line header" />
                            <h4 className="header">
                                   Student Repositories Directory
                            </h4>
                            <input type="text" className="h3-size" value={this.props.courseID} onChange={this.onChange} name="name" autoComplete="off"/>
                            <h4 className="header">
                                   Repositories Update Interval
                            </h4>
                            <input type="number" className="h3-size" value={this.state.interval} onChange={this.onChange} name="interval" ref="interval"/>
                            { modal_buttons }
                        </div>
                    } />


                <div className={`modal-overlay${ this.props.show ? ' show' : '' }`}
                    style={ this.props.show ? { } : { 'display': 'none' } }
                    onClick={ this.props.close } />
            </div>
        )
    }

}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        directory_list: state.course && state.course.getDirectoryListData ? state.course.getDirectoryListData : [],
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setDirectory: (url, headers, body) => dispatch(setDirectory(url, headers, body)),
        modifyProject: (url, headers, body) => dispatch(modifyProject(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CourseModal)