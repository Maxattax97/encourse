import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Card, Checkbox, CheckmarkIcon, Summary, Title} from '../../Helpers'
import {fuzzing} from '../../../fuzz'
import {history} from '../../../redux/store'
import {getStudentPreviews, setCurrentStudent} from '../../../redux/actions'
import PreviewCard from "../common/PreviewCard"

class CourseStudentSummary extends Component {

    constructor(props) {
        super(props)

        //TODO Jordan Buckmaster : Please move into props for other components to interact with these states
        this.state = {
            card_selected: false,
            cards_selected: {}
        }
    }

    clickStudentCard = (student) => {
        if(this.state.card_selected)
            this.setState({ card_selected: false, cards_selected: {} })
        else {
            this.props.setCurrentStudent(student)
            if (fuzzing) {
                // NOTE: we don't even use the student id in the url
                history.push('/student/student')
            } else {
                history.push(`/student/${student.id}`)
            }
        }

    };

    clickStudentSelect = (event, student) => {
        event.stopPropagation()

        if(!this.state.cards_selected[student.id])
            this.state.cards_selected[student.id] = true
        else {
            delete this.state.cards_selected[student.id]

            if(Object.keys(this.state.cards_selected).length === 0) {
                this.setState({ card_selected: false, cards_selected: {} })
                return
            }
        }

        this.setState({ card_selected: true, cards_selected: this.state.cards_selected })
    }

    render() {
        return (
            <Summary columns={ 5 }>
                {
                    this.props.students.map( (student) =>
	                    <PreviewCard onClick={ () => this.clickStudentCard(student) } isSelected={ this.state.cards_selected[student.id] } key={ student.id }>
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
		                    <Checkbox className={ this.state.card_selected ? 'card-select selectable' : 'card-select' } onClick={ (e) => this.clickStudentSelect(e, student) }>
			                    {
				                    this.state.cards_selected[student.id] ?
					                    <CheckmarkIcon/>
					                    : null
			                    }
		                    </Checkbox>
	                    </PreviewCard>
                    )
                }
            </Summary>
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