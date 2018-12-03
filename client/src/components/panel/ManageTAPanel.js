import React, { Component } from 'react'
import { connect } from 'react-redux'

import ActionNavigation from '../navigation/ActionNavigation'
import TANavigation from '../navigation/TANavigation'
import {Summary, Title, CheckmarkIcon, BackNav} from '../Helpers'
import {history} from '../../redux/store'
import StudentAssignPreview from './manage-ta/StudentAssignPreview'
import SectionPreview from './manage-ta/SectionPreview'
import url from '../../server'
import {getSectionsData, getStudentPreviews, getTeachingAssistants, submitStudents, updateStudentsPage, resetStudentsPage } from '../../redux/actions'
import { getCurrentCourseId, getCurrentSemesterId } from '../../redux/state-peekers/course'

class ManageTAPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            current_ta: 0,
            assignment_type: 2,
            students: [],
            sections: [],
            student_search: ''
        }
    }

    componentDidMount = () => {
        //TODO: clear class projects/student previews to account for multiple classes
        //TODO: Add course ID functionality for multiple classes
        this.props.getSectionsData(`${url}/api/sectionsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}`)
        this.props.getTeachingAssistants(`${url}/api/teachingAssistantsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}`)
        this.props.getStudentPreviews(`${url}/api/studentsData?size=10&page=1&courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}`)
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
    };

    submitStudentSearch = (event) => {
        if(event.key === 'Enter') {
            if(this.props.students.find(e => e.id === this.state.student_search))
                this.state.students.push(this.state.student_search)
            this.setState({ 'student_search': '', students: this.state.students })
        }
    }

    changeTA = (index) => {
        //this.state.students = [].concat(this.props.teaching_assistants[index].students)
        const sections = [].concat(this.props.teaching_assistants[index].sections)

        this.setState({
            assignment_type: this.props.teaching_assistants[index].assignment_type,
            students: this.state.students,
            sections,
            current_ta: index
        })
    }

    back = () => {
        history.goBack()
    }

    toggleSection = (id) => {
        if(this.state.sections.includes(id))
            this.state.sections.splice(this.state.sections.indexOf(id), 1)
        else
            this.state.sections.push(id)

        this.setState({ sections: this.state.sections })
    }

    toggleStudent = (id) => {
        if(this.state.students.includes(id))
            this.state.students.splice(this.state.students.indexOf(id), 1)
        else
            this.state.students.push(id)

        this.setState({ students: this.state.students })
    }

    changed = () => {
        const ta = this.props.teaching_assistants[this.state.current_ta]
        if(!ta)
            return false

        return this.state.assignment_type !== ta.assignment_type ||
            this.state.students.length < ta.students.length ||
            this.state.students.filter(student => !ta.students.includes(student)).length > 0 ||
            this.state.sections.length < ta.sections.length ||
            this.state.sections.filter(section => !ta.sections.includes(section)).length > 0
    }

    discard = () => {
        const students = [].concat(this.props.teaching_assistants[this.state.current_ta].students)
        const sections = [].concat(this.props.teaching_assistants[this.state.current_ta].sections)

        this.setState({
            assignment_type: this.props.teaching_assistants[this.state.current_ta].assignment_type,
            students,
            sections
        })
    }

    save = () => {
        const ta = this.props.teaching_assistants[this.state.current_ta].id
   
        //TODO: add variable semester and class id
        for(let id of this.state.sections) {
            this.props.submitStudents(`${url}/api/add/studentsToTA?sectionID=${id}`, JSON.stringify({
                [ta]: this.state.students
            }), { ta, students: this.state.students })
        }
    }

    scrolledToBottom = () => {
        if(!this.props.last) {
            this.props.getStudentPreviews(`${url}/api/studentsData?courseID=${this.props.currentCourseId}&semester=${this.props.currentSemesterId}&size=10&page=${this.props.page + 1}&projectID=${this.props.currentProjectId}`)
            this.props.updateStudentsPage()
        }
    }

    render() {
        const current_ta = this.props.teaching_assistants[this.state.current_ta] ? this.props.teaching_assistants[this.state.current_ta] : false


        const action_names = [
            (this.changed() ? '*' : '') + 'Discard Changes',
            (this.changed() ? '*' : '') + 'Save Changes'
        ]

        const actions = [
            this.discard,
            this.save
        ]

        if(!current_ta)
            return (
                <div className='manage-ta-panel'>

                    <div className='panel-left-nav'>
                        <BackNav back='Course' backClick={ this.back }/>
                        <ActionNavigation
                            actions={ actions }
                            action_names={ action_names } />

                        <TANavigation
                            teaching_assistants={ this.props.teaching_assistants }
                            isLoading={ this.props.taIsLoading }
                            current_ta={ this.state.current_ta }
                            change={ this.changeTA }/>
                    </div>

                    <div className='panel-right-nav'>
                        <div className='top-nav' />
                    </div>

                    <div className="panel-center-content">
                        <Title>
                            <h1 className='header'>{this.props.currentCourseId.toUpperCase()} - Teaching Assistants</h1>
                        </Title>
                        <div className='h1 break-line header' />

                    </div>
                </div>
            )

        return (
            <div className='manage-ta-panel'>

                <div className='panel-left-nav'>
                    <BackNav back='Course' backClick={ this.back }/>
                    <ActionNavigation
                        actions={ actions }
                        action_names={ action_names } />

                    <TANavigation
                        teaching_assistants={ this.props.teaching_assistants }
                        isLoading={ this.props.taIsLoading }
                        current_ta={ this.state.current_ta }
                        change={ this.changeTA }/>
                </div>

                <div className='panel-right-nav'>
                    <div className='top-nav' />
                </div>

                <div className="panel-center-content">
                    <h1 className='header'>{this.props.currentCourseId.toUpperCase()} - Teaching Assistants - { `${current_ta.first_name} ${current_ta.last_name}` }</h1>
                    <div className='h1 break-line header' />

                    <h3 className='header'>Assigning Sections to {current_ta.first_name} {current_ta.last_name}</h3>
                    <Summary columns={5} isLoading={this.props.sectionsIsLoading}>
	                    {
	                    	this.props.sections.map( (section) =>
			                    <SectionPreview key={section.id}
			                                    section={section}
			                                    onClick={ () => this.toggleSection(section.id)}
			                                    isSelected={this.state.sections.includes(section.id)}/>
		                    )
	                    }
                    </Summary>

                    <div className='h2 break-line header' />
                    <h3 className='header'>Assigning Students to {current_ta.first_name} {current_ta.last_name}</h3>
                    <div className='summary-container'>
                        <div className='float-height cols-2'>
                            <div>
                                <div className={this.state.assignment_type === 0 ? 'action radio-selected' : 'action'}
                                    onClick={ () => this.setState({ assignment_type: 0 }) }>

                                    <h5 className="header">From Purdue Career Account</h5>
                                    {
                                        this.state.assignment_type === 0 ?
                                            <CheckmarkIcon /> :
                                            null
                                    }
                                </div>
                                <div className={this.state.assignment_type === 1 ? 'action radio-selected' : 'action'}
                                    onClick={ () => this.setState({ assignment_type: 1 }) }>

                                    <h5 className="header">From student summary cards</h5>
                                    {
                                        this.state.assignment_type === 1 ?
                                            <CheckmarkIcon /> :
                                            null
                                    }
                                </div>
                                <div className={this.state.assignment_type === 2 ? 'action radio-selected' : 'action'}
                                    onClick={ () => this.setState({ assignment_type: 2 }) }>

                                    <h5 className="header">From all students in assigned sections</h5>
                                    {
                                        this.state.assignment_type === 2 ?
                                            <CheckmarkIcon /> :
                                            null
                                    }
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className='h2 break-line' />

                    {
                        this.state.assignment_type === 0 ?
                            <div className='career-account-list'>
                                <div className='summary-container'>
                                    <div className='float-height cols-5'>
                                        <div>
                                            <h4 className='header'>Student Career Account</h4>
                                            <input type="text" className="h3-size" value={this.state.student_search} onChange={this.onChange} onKeyPress={this.submitStudentSearch} name="student_search" autoComplete="off"/>
                                        </div>
                                    </div>
                                </div>
                                <Summary columns={ 5 } isLoading={this.props.studentsIsLoading}>
                                    {
                                        this.state.students.map( (student) =>
	                                        <StudentAssignPreview key={student} student={this.props.students.find(e => e.id === student)} isSelected={true}/>
                                        )
                                    }
                                </Summary>
                            </div>

                            : this.state.assignment_type === 1 ?
                                <div className='student-selection-list'>
                                    <Summary columns={ 5 } isLoading={this.props.studentsIsLoading}>
                                        {
	                                        this.props.students.filter(student => student.sections.filter(id => this.state.sections.includes(id)).length > 0).map( (student) =>
		                                        <StudentAssignPreview key={student.id} onClick={() => this.toggleStudent(student.id)} student={student} isSelected={this.state.students.includes(student.id)}/>
                                            )
                                        }
                                    </Summary>
                                </div>

                                :
                                <div className='student-selection-list'>
                                    <Summary columns={ 5 } isLoading={this.props.studentsIsLoading}>
                                        {
	                                        this.props.students.filter(student => student.sections.filter(id => this.state.sections.includes(id)).length > 0).map( (student) =>
		                                        <StudentAssignPreview key={student.id} student={student} isSelected={true}/>
                                            )
                                        }
                                    </Summary>
                                </div>
                    }

                </div>

            </div>)
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : [],
        sections: state.course && state.course.getSectionsData ? state.course.getSectionsData : [],
        teaching_assistants: state.teachingAssistant && state.teachingAssistant.getTeachingAssistantsData ? state.teachingAssistant.getTeachingAssistantsData : [],
        sectionsIsLoading: state.course ? state.course.getSectionsIsLoading : false,
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
        taIsLoading: state.teachingAssistant ? state.teachingAssistant.getTeachingAssistantsIsLoading : false,
        studentsIsLoading: state.course ? state.course.getStudentPreviewsIsLoading : false,
        page: state.course && state.course.studentsPage ? state.course.studentsPage : 1,
        last: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.last : true,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        getSectionsData: (url, headers, body) => dispatch(getSectionsData(url, headers, body)),
        getTeachingAssistants: (url, headers, body) => dispatch(getTeachingAssistants(url, headers, body)),
        submitStudents: (url, body, data) => dispatch(submitStudents(url, null, body, data)),
        updateStudentsPage: () => dispatch(updateStudentsPage()),
        resetStudentsPage: () => dispatch(resetStudentsPage()),
    }
}


export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(ManageTAPanel)