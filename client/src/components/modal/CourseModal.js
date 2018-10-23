import React, { Component } from 'react'
import { connect } from 'react-redux'

import Modal from './Modal'
// import deleteIcon from '../../img/trash.svg'
import { setDirectory, modifyProject } from '../../redux/actions'
import url from '../../server'
import {Title} from '../Helpers'
import {checkmark} from '../../helpers/icons'

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
        return (
            <div className="course-modal">
                <Modal center
                    show={ this.props.show }
                    onExit={ this.props.close }
                    content={
                        [
                            <Title header={<h2 className='header'>Course Settings</h2> } key={1}/>,
                            <div className="h2 break-line header" key={2}/>,
                            <h4 className="header" key={3}>
                                   Student Repositories Directory
                            </h4>,
                            <input type="text" className="h3-size" value={this.props.courseID} onChange={this.onChange} name="name" autoComplete="off" key={4}/>,
                            <h4 className="header" key={5}>
                                   Repositories Update Interval
                            </h4>,
                            <input type="number" className="h3-size" value={this.state.interval} onChange={this.onChange} name="interval" ref="interval" key={6}/>,
                            <div className="modal-buttons float-height" key={7}>
                                <div className="svg-icon action" onClick={ this.saveSettings }>
                                    <img className='svg-icon' src={ checkmark.icon } alt={ checkmark.alt_text } />
                                </div>
                            </div>
                        ]
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