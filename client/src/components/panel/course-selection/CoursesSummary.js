import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Title} from '../../Helpers'
import {history} from '../../../redux/store'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {getFilters} from '../../../redux/state-peekers/control'

class CoursesSummary extends Component {

    /*componentDidMount() {
        if(this.props.project)
            retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveAllStudents(this.props.project, this.props.course, this.props.semester)
    }*/

    clickCourseCard = (course) => {
        history.push(`/${course.id}/${course.semester}/course`)
    };

    renderPreview = (course) => {
        return (
            <div>
                <Title>
                    <h4>{ course.title }</h4>
                    <h4>{ course.semester }</h4>
                </Title>
                <div className="h4 break-line header" />
                <div className="preview-content">
                    <h5>Prof:     { course.professor }</h5>
                    <h5>TAs:      { course.teaching_assistants }</h5>
                    <h5>Students: { course.students }</h5>
                </div>
            </div>
        )
    }

    render() {
        return (
            <SelectableCardSummary type='courses'
                                   values={this.props.courses}
                                   render={this.renderPreview}
                                   onClick={this.clickCourseCard} />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        courses: [],
        filters: getFilters(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursesSummary)