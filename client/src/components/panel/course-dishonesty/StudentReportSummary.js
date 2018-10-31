import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Summary, Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {history} from '../../../redux/store'
import {getStudentPreviews, setCurrentStudent} from '../../../redux/actions'

class StudentReportSummary extends Component {

    clickStudentCard = (student) => {
        this.props.setCurrentStudent(student)
        if (fuzzing) {
            // NOTE: we don't even use the student id in the url
            history.push('/student-dishonesty/student')
        } else {
            history.push(`/student-dishonesty/${student.id}`)
        }
    }

    render() {
        return (
            <Summary columns={ 5 }
                data={ this.props.students }
                className='course-students-report'
                iterator={ (student) =>
                    <Card className='action' onClick={ () => this.clickStudentCard(student) } key={student.id}>
                        <div className="summary-preview">
                            <Title>
                                <h4>{ student.first_name }</h4>
                                <h4>{ student.last_name }</h4>
                            </Title>
                            <div className="h4 break-line header" />
                            <div className="preview-content">
                                <h5>Score: { student.timeSpent[this.props.currentProjectId] }</h5>
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


export default connect(mapStateToProps, mapDispatchToProps)(StudentReportSummary)