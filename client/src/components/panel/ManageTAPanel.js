import React, { Component } from 'react'
import { connect } from 'react-redux'

import ActionNavigation from '../navigation/ActionNavigation'
import TANavigation from '../navigation/TANavigation'
import url from '../../server'
import { submitStudents} from '../../redux/actions'
import {
    getCurrentCourseId,
    getCurrentSemesterId,
    getCurrentTA,
    getSections,
    getStudents, getTeachingAssistants
} from '../../redux/state-peekers/course'
import SectionSummary from './manage-ta/SectionSummary'
import StudentAssignFilter from './manage-ta/StudentAssignFilter'
import BackNavigation from '../navigation/BackNavigation'

class ManageTAPanel extends Component {

    discard = () => {
        const students = [].concat(this.props.ta.students)
        const sections = [].concat(this.props.ta.sections)

        this.setState({
            assignment_type: this.props.ta.assignment_type,
            students,
            sections
        })
    }

    save = () => {
        const ta = this.props.ta.id
   
        //TODO: add variable semester and class id
        for(let id of this.state.sections) {
            this.props.submitStudents(`${url}/api/add/studentsToTA?sectionID=${id}`, JSON.stringify({
                [ta]: this.state.students
            }), { ta, students: this.state.students })
        }
    }

    render() {
        const action_names = [
            'Discard Changes',
            'Save Changes'
        ]

        const actions = [
            this.discard,
            this.save
        ]

        return (
            <div className='manage-ta-panel'>

                <div className='panel-left-nav'>
                    <BackNavigation/>

                    <TANavigation />

                    <ActionNavigation
                        actions={ actions }
                        action_names={ action_names } />
                </div>

                {
                    this.props.teaching_assistants.data.length ?
                        this.props.ta ?
                            <div className="panel-center-content">
                                <h1 className='header'>{ `${this.props.ta.first_name} ${this.props.ta.last_name} (${this.props.ta.id})` }</h1>
                                <div className='h1 break-line header' />

                                <h3 className='header'>Assign Sections</h3>
                                <SectionSummary/>

                                <div className='h1 break-line header' />
                                <StudentAssignFilter/>

                            </div>
                            :
                            <div className="panel-center-content">
                                <h1 className='header'>{ 'Select a Teaching Assistant' }</h1>
                                <div className='h1 break-line header' />
                            </div>
                        :
                        <div className="panel-center-content">
                            <h1 className='header'>{ 'No Teaching Assistants Assigned' }</h1>
                            <div className='h1 break-line header' />
                        </div>
                }



            </div>)
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        sections: getSections(state),
        teaching_assistants: getTeachingAssistants(state),
        ta: getCurrentTA(state),
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        submitStudents: (url, body, data) => dispatch(submitStudents(url, null, body, data)),
    }
}


export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(ManageTAPanel)