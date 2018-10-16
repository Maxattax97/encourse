import React, { Component } from 'react'
import { connect } from 'react-redux'

class StudentPreview extends Component {
    render() {
        const percent = this.props.isHidden ? this.props.student.hiddenGrades[this.props.projectID] : this.props.student.grades[this.props.projectID]
        return (
            <div className="student-preview">
                <div className="title">
                    <h4>{this.props.student.first_name}</h4>
                    <h4>{this.props.student.last_name}</h4>
                </div>
                <div className="h4 break-line header" />
                <div className="student-preview-commits">
                    <h5>Time: {this.props.student.timeSpent[this.props.projectID]}</h5>
                    <h5>Commits: {this.props.student.commitCounts[this.props.projectID]}</h5>
                </div>
                <div className="student-preview-progress">
                    <div className="progress-bar">
                        <div style={{width: percent + '%'}} />
                    </div>
                    <h6 className="progress-text">
                        {parseInt(percent)}%
                    </h6>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        isHidden: state.projects ? state.projects.isHidden : false,
    }
}

export default connect(mapStateToProps, null)(StudentPreview)