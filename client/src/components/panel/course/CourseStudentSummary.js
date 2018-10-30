import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Summary, Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {history} from '../../../redux/store'
import {getStudentPreviews, setCurrentProject, setCurrentStudent, setModalState} from '../../../redux/actions'

class CourseStudentSummary extends Component {

    showStudentPanel = (student) => {
        this.props.setCurrentStudent(student)
        if (fuzzing) {
            // NOTE: we don't even use the student id in the url
            history.push('/student/student')
        } else {
            history.push(`/student/${student.id}`)
        }
    };

    render() {
        return (
            <Summary
                columns={ 5 }
                data={ this.props.students }
                className='course-students'
                iterator={ (student) =>
                    <Card onClick={() => this.showStudentPanel(student)} className='action' key={student.id}>
                        <div className="summary-preview">
                            <Title>
                                <h4>{ student.first_name }</h4>
                                <h4>{ student.last_name }</h4>
                            </Title>
                            <div className="h4 break-line header" />
                            <div className="preview-content">
                                <h5>Time: { student.timeSpent[this.props.currentProjectId] } hours</h5>
                                <h5>Commits: { student.commitCounts[this.props.currentProjectId] }</h5>
                            </div>
                            <div className="student-preview-progress">
                                <div className="progress-bar">
                                    <div style={{width: (this.props.isHidden ? student.hiddenGrades[this.props.currentProjectId] : student.grades[this.props.currentProjectId]) + '%'}} />
                                </div>
                                <h6 className="progress-text">
                                    {parseInt(this.props.isHidden ? student.hiddenGrades[this.props.currentProjectId] : student.grades[this.props.currentProjectId])}%
                                </h6>
                            </div>
                        </div>
                    </Card>
                } />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        isHidden: state.projects ? state.projects.isHidden : false,
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CourseStudentSummary)