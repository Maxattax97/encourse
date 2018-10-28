import React, { Component } from 'react'
import { connect } from 'react-redux'

import ActionNavigation from '../navigation/ActionNavigation'
import TANavigation from '../navigation/TANavigation'
import {Summary, Title, CheckmarkIcon} from '../Helpers'
import {history} from '../../redux/store'
import StudentAssignPreview from './util/StudentAssignPreview'
import SectionPreview from './util/SectionPreview'
import url from '../../server'
import {getSectionsData, getStudentPreviews, getTeachingAssistants} from '../../redux/actions'

class ManageTAPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            /*teaching_assistants: [
                {
                    first_name: 'Killian',
                    last_name: 'Le Clainche'
                    id: 'kleclain',
                    assignment_type: 2,
                    students: [],
                    sections: []
                },
                {
                    first_name: 'Jordan',
                    id: 'jmbuck',
                    assignment_type: 1,
                    students: ['heo1', 'heo2', 'heo3'],
                    sections: ['0']
                }
            ],*/
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
        this.props.getSectionsData(`${url}/api/sectionsData?courseID=cs252&semester=Fall2018`)
        this.props.getTeachingAssistants(`${url}/api/teachingAssistantsData?courseID=cs252&semester=Fall2018`)
        this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018`)
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
        this.state.sections = [].concat(this.props.teaching_assistants[index].sections)

        this.setState({
            assignment_type: this.props.teaching_assistants[index].assignment_type,
            students: this.state.students,
            sections: this.state.sections,
            current_ta: index
        })
    }

    back = () => {
        history.push('/course')
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
        this.state.students = [].concat(this.props.teaching_assistants[this.state.current_ta].students)
        this.state.sections = [].concat(this.props.teaching_assistants[this.state.current_ta].sections)

        this.setState({
            assignment_type: this.props.teaching_assistants[this.state.current_ta].assignment_type,
            students: this.state.students,
            sections: this.state.sections
        })
    }

    save = () => {

    }

    render() {
        const current_ta = this.props.teaching_assistants[this.state.current_ta] ? this.props.teaching_assistants[this.state.current_ta] : false

        if(!current_ta)
            return (
                <div className='manage-ta-panel'>
                    <TANavigation
                        back="Course"
                        backClick={ this.back }
                        teaching_assistants={ this.props.teaching_assistants }
                        current_ta={ this.state.current_ta }
                        change={ this.changeTA }/>

                    <div className='panel-right-nav'>
                        <div className='top-nav' />
                        <ActionNavigation
                            actions={[
                                this.discard,
                                this.save
                            ]}
                            action_names={[
                                (this.changed() ? '*' : '') + 'Discard Changes',
                                (this.changed() ? '*' : '') + 'Save Changes'
                            ]} />
                    </div>

                    <div className="panel-center-content">
                        <Title>
                            <h1 className='header'>CS252 - Teaching Assistants</h1>
                        </Title>
                        <div className='h1 break-line header' />

                    </div>
                </div>
            )

        return (
            <div className='manage-ta-panel'>
                <TANavigation
                    back="Course"
                    backClick={ this.back }
                    teaching_assistants={ this.props.teaching_assistants }
                    isLoading={ this.props.taIsLoading }
                    current_ta={ this.state.current_ta }
                    change={ this.changeTA }/>

                <div className='panel-right-nav'>
                    <div className='top-nav' />
                    <ActionNavigation
                        actions={[
                            this.discard,
                            this.save
                        ]}
                        action_names={[
                            (this.changed() ? '*' : '') + 'Discard Changes',
                            (this.changed() ? '*' : '') + 'Save Changes'
                        ]} />
                </div>

                <div className="panel-center-content">
                    <h1 className='header'>CS252 - Teaching Assistants - { `${current_ta.first_name} ${current_ta.last_name}` }</h1>
                    <div className='h1 break-line header' />

                    <h3 className='header'>Assigning Sections to {current_ta.first_name} {current_ta.last_name}</h3>
                    <Summary
                        columns={5}
                        data={this.props.sections}
                        isLoading={this.props.sectionsIsLoading}
                        iterator={(section) =>
                            current_ta ?
                                <SectionPreview key={section.id}
                                    section={section}
                                    onClick={ () => this.toggleSection(section.id)}
                                    isSelected={this.state.sections.includes(section.id)}/>
                                : null
                        }
                    />

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
                                <Summary columns={ 5 }
                                    data={ this.state.students }
                                    isLoading={this.props.studentsIsLoading}
                                    iterator={(student) =>
                                        <StudentAssignPreview key={student} student={this.props.students.find(e => e.id === student)} isSelected={true}/>
                                    } />
                            </div>

                            : this.state.assignment_type === 1 ?
                                <div className='student-selection-list'>
                                    <Summary columns={ 5 }
                                        data={ this.props.students.filter(student => student.sections.filter(id => this.state.sections.includes(id)).length > 0) }
                                        isLoading={this.props.studentsIsLoading}
                                        iterator={(student) =>
                                            <StudentAssignPreview key={student.id} onClick={() => this.toggleStudent(student.id)} student={student} isSelected={this.state.students.includes(student.id)}/>
                                        } />
                                </div>

                                :
                                <div className='student-selection-list'>
                                    <Summary columns={ 5 }
                                        data={ this.props.students.filter(student => student.sections.filter(id => this.state.sections.includes(id)).length > 0) }
                                        isLoading={this.props.studentsIsLoading}
                                        iterator={(student) =>
                                            <StudentAssignPreview key={student.id} student={student} isSelected={true}/>
                                        } />
                                </div>
                    }

                </div>

            </div>)
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        sections: state.course && state.course.getSectionsData ? state.course.getSectionsData : [],
        teaching_assistants: state.teachingAssistant && state.teachingAssistant.getTeachingAssistantsData ? state.teachingAssistant.getTeachingAssistantsData : [],
        sectionsIsLoading: state.course ? state.course.getSectionsIsLoading : false,
        taIsLoading: state.teachingAssistant ? state.teachingAssistant.getTeachingAssistantsIsLoading : false,
        studentsIsLoading: state.course ? state.course.getStudentPreviewsIsLoading : false,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        getSectionsData: (url, headers, body) => dispatch(getSectionsData(url, headers, body)),
        getTeachingAssistants: (url, headers, body) => dispatch(getTeachingAssistants(url, headers, body))
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(ManageTAPanel)