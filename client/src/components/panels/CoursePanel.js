import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import { getStudentPreviews, getClassProjects, setCurrentProject, setCurrentStudent, setModalBlur } from '../../redux/actions/index'
import ProjectNavigation from '../navigation/ProjectNavigation'
import Card from '../Card'
import StudentPreview from './util/StudentPreview'
import ClassProgressHistogram from '../charts/ClassProgressHistogram'
import settingsIcon from "../../img/settings.svg";
import ActionNavigation from "../navigation/ActionNavigation";

class CoursePanel extends Component {

    constructor(props) {
        super(props);

        this.state = {
            modal_blur: ""
        };
    }

    componentDidMount = () => {
        //TODO: clear class projects/student previews to account for multiple classes
        //TODO: Add course ID functionality for multiple classes
        this.props.getClassProjects(`${url}/secured/projectsData?courseID=cs252&semester=Fall2018`, 
        {'Authorization': `Bearer ${this.props.token}`});
        this.props.getStudentPreviews(`${url}/secured/studentsData?courseID=cs252&semester=Fall2018`, 
            {'Authorization': `Bearer ${this.props.token}`})
    }

    showStudentPanel = (student) => {
        //TODO: move this setCurrentStudent to StudentPanel, store all students in an array in redux
        this.props.setCurrentStudent(student);
        history.push(`/student/${student.id}`)
    };

    render() {
        return (
            <div className="panel-course">
                <ProjectNavigation info={this.props.projects}
                                   onModalBlur={(blur) => this.setState({modal_blur : blur ? " blur" : ""})}
                                   {...this.props}/>

                <div className="panel-center-content">

                    <div className={ `panel-course-content${this.state.modal_blur}` }>
                        <div className="title">
                            <h1>CS252</h1>
                            <img src={ settingsIcon }/>
                        </div>
                        <h1 className="break-line title" />
                        <h3>Course Charts</h3>
                        <div className="charts float-height">
                            <Card component={<ClassProgressHistogram projectID={this.props.currentProjectId}/>} />
                        </div>
                        <h2 className="break-line" />
                        <h3>Students</h3>
                        <div className="panel-course-students float-height">
                            {
                                this.props.students && 
                                this.props.students.map((student) =>
                                    <Card key={student.id}
                                          component={<StudentPreview student={student} projectID={this.props.currentProjectId}
                                                                     setCurrentProject={this.props.setCurrentProject} />}
                                          onClick={() => this.showStudentPanel(student)}/>)
                            }
                        </div>
                    </div>
                </div>

                <div className="panel-right-nav">
                    <div className={ `panel-student-side-content${this.state.modal_blur}` }>
                        <ActionNavigation />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        projects: state.projects && state.projects.getClassProjectsData ? state.projects.getClassProjectsData : [],
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : 0
    }
};
  
const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        getClassProjects: (url, headers, body) => dispatch(getClassProjects(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(CoursePanel);
