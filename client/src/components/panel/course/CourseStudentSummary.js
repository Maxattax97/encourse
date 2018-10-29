import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Summary, Title} from '../../Helpers'

class CourseStudentSummary extends Component {
    render() {
        return (
            <Summary
                columns={ 5 }
                data={ this.props.students }
                className='course-students'
                iterator={ (student) =>
                    <Card onClick={this.props.onClick} className='action'>
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

export default connect(mapStateToProps, null)(CourseStudentSummary)