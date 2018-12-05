import React, { Component } from 'react'
import { connect } from 'react-redux'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {getCurrentCourseId, getCurrentSemesterId, getCurrentTA, getSections} from '../../../redux/state-peekers/course'
import {toggleSelectCard} from '../../../redux/actions'
import {retrieveAllSections} from '../../../redux/retrievals/course'

class SectionSummary extends Component {

    componentDidMount() {
        if(this.props.course && this.props.semester)
            retrieveAllSections(this.props.course, this.props.semester)
    }

    renderPreview = (section) => {

        return (
            <div>
                <div className="title">
                    <h4>{section.name}</h4>
                    <h4>{section.time}</h4>
                </div>
                <div className="h4 break-line header" />
                <div className="preview-content">
                    <div className="stat float-height">
                        <h5>Students</h5>
                        <h5>{section.students.length}</h5>
                    </div>
                    <div className="stat float-height">
                        <h5>TAs</h5>
                        <h5>{section.teaching_assistants.length}</h5>
                    </div>
                </div>
            </div>
        )
    }

    render() {
        if(!this.props.ta || !this.props.sections.data)
            return null

        return (
            <SelectableCardSummary type='sections'
                                   values={this.props.sections.data}
                                   render={this.renderPreview}
                                   onClick={(value) => {
                                       console.log(value)
                                       this.props.toggleSelectCard('sections', value.id)
                                   }}
                                   noReset />
        )
    }
}

const mapStateToProps = (state) => {
    return {
        sections: getSections(state),
        ta: getCurrentTA(state),
        course: getCurrentCourseId(state),
        semester: getCurrentSemesterId(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleSelectCard: (id, index) => dispatch(toggleSelectCard(id, index)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(SectionSummary)