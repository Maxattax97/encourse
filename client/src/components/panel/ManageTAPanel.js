import React, { Component } from 'react'

import ActionNavigation from '../navigation/ActionNavigation'
import TANavigation from '../navigation/TANavigation'
import {Summary, Title, Card, CheckmarkIcon} from '../Helpers'
import {history} from '../../redux/store'
import StudentAssignPreview from './util/StudentAssignPreview'
import SectionPreview from './util/SectionPreview'

class ManageTAPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            all_sections: [
                {
                    name: 'Section 1',
                    id: '0',
                    time: 'F 3:30 - 5:20',
                    students: 27,
                    teaching_assistants: 2
                },
                {
                    name: 'Section 2',
                    id: '1',
                    time: 'W 3:30 - 5:20',
                    students: 24,
                    teaching_assistants: 3
                },
                {
                    name: 'Section 3',
                    id: '2',
                    time: 'W 1:30 - 3:20',
                    students: 21,
                    teaching_assistants: 1
                }
            ],
            teaching_assistants: [
                {
                    name: 'Killian Le Clainche',
                    id: 'kleclain',
                    assignment_type: 2,
                    students: ['hello1', 'hello2', 'hello3'],
                    sections: ['0', '2']
                },
                {
                    name: 'Jordan',
                    id: 'jmbuck',
                    assignment_type: 1,
                    students: ['heo1', 'heo2', 'heo3'],
                    sections: ['0']
                }
            ],
            current_ta: 0,
            assignment_type: 2,
            students: ['hello1', 'hello2', 'hello3'],
            sections: ['0', '2']
        }
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

    changed = () => {
        const ta = this.state.teaching_assistants[this.state.current_ta]

        console.log(this.state.assignment_type !== ta.assignment_type, this.state.students.length < ta.students.length, this.state.students.filter(student => !ta.students.includes(student)).length > 0)
        return this.state.assignment_type !== ta.assignment_type ||
            this.state.students.length < ta.students.length ||
            this.state.students.filter(student => !ta.students.includes(student)).length > 0 ||
            this.state.sections.length < ta.sections.length ||
            this.state.sections.filter(section => !ta.sections.includes(section)).length > 0
    }

    discard = () => {
        this.state.students = []

        this.state.teaching_assistants[this.state.current_ta].students.forEach(student => this.state.students.push(student))

        this.setState({
            assignment_type: this.state.teaching_assistants[this.state.current_ta].assignment_type,
            students: this.state.students
        })
    }

    save = () => {

    }

    render() {
        const current_ta = this.state.teaching_assistants[this.state.current_ta] ? this.state.teaching_assistants[this.state.current_ta] : false

        return (
            <div className='manage-ta-panel'>
                <TANavigation
                    back="Course"
                    backClick={ this.back }/>

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
                    <Title header={ <h1 className='header'>CS252 - Teaching Assistants{current_ta ? ' - ' + current_ta.name : ''}</h1> } />
                    <div className='h1 break-line header' />
                    <Summary header={<h3 className='header'>Assigning Sections</h3>}
                        columns={5}
                        data={this.state.all_sections}
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
                    <h3 className='header'>Assigning Students</h3>
                    <div className='summary-container'>
                        <div className='float-height cols-2'>
                            <div>
                                <div className={this.state.assignment_type === 0 ? 'action radio-selected' : 'action'}
                                    onClick={ () => this.setState({ assignment_type: 0 }) }>

                                    <h5 className="header">Assign by career account</h5>
                                    {
                                        this.state.assignment_type === 0 ?
                                            <CheckmarkIcon /> :
                                            null
                                    }
                                </div>
                                <div className={this.state.assignment_type === 1 ? 'action radio-selected' : 'action'}
                                    onClick={ () => this.setState({ assignment_type: 1 }) }>

                                    <h5 className="header">Assign by selection</h5>
                                    {
                                        this.state.assignment_type === 1 ?
                                            <CheckmarkIcon /> :
                                            null
                                    }
                                </div>
                                <div className={this.state.assignment_type === 2 ? 'action radio-selected' : 'action'}
                                    onClick={ () => this.setState({ assignment_type: 2 }) }>

                                    <h5 className="header">Assign all students</h5>
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
                                            <h4 className='header'>Student ID</h4>
                                            <input type="text" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="name" autoComplete="off"/>
                                        </div>
                                    </div>
                                </div>
                                <Summary columns={ 5 }
                                    data={ this.state.students }
                                    iterator={(student) =>
                                        <StudentAssignPreview student={student}/>
                                    } />
                            </div>

                            : this.state.assignment_type === 1 ?
                                <div className='student-selection-list'>
                                </div>

                                : null
                    }

                </div>

            </div>)
    }
}

export default ManageTAPanel