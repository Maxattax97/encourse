import React, { Component } from 'react'

import ActionNavigation from '../navigation/ActionNavigation'
import TANavigation from '../navigation/TANavigation'
import {Summary, Title, Card, CheckmarkIcon} from '../Helpers'
import {history} from '../../redux/store'
import StudentAssignPreview from './util/StudentAssignPreview'

class ManageTAPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            teaching_assistants: [
                {
                    name: 'Killian Le Clainche',
                    id: 'kleclain',
                    assignment_type: 2,
                    students: ['hello1', 'hello2', 'hello3']
                },
                {
                    name: 'Jordan',
                    id: 'jmbuck',
                    assignment_type: 1,
                    students: ['heo1', 'heo2', 'heo3']
                }
            ],
            current_ta: 0,
            assignment_type: 2,
            students: ['hello1', 'hello2']
        }
    }

    back = () => {
        history.push('/course')
    }

    changed = () => {
        const ta = this.state.teaching_assistants[this.state.current_ta]
        return this.state.assignment_type !== ta.assignment_type ||
            this.state.students.length < ta.students.length ||
            this.state.students.filter(student => !ta.students.includes(student)).length > 0
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
        return (<div className='manage-ta-panel'>
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
                <Title header={ <h1 className='header'>CS252 - Teaching Assistants</h1> } />
                <div className='h1 break-line header' />

                {
                    this.state.teaching_assistants[this.state.current_ta] ?
                        <Summary
                            header={
                                <h3 className='header'>
                                    { (this.changed() ? '*' : '') + this.state.teaching_assistants[this.state.current_ta].name + ' - ' }Assigning Students
                                </h3>
                            }
                            columns={ 2 }
                            data={ [ 1, 2 ] }
                            iterator={ (index) =>
                                index === 1 ?
                                    <div key={index}>
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
                                    </div> :

                                    null
                            } /> :
                        <h3 className='header'>
                            N/A
                        </h3>
                }
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
                            <Summary columns={ 2 }
                                data={ this.state.students }
                                iterator={(index) =>
                                    <Card key={index} onClick={ () => {} }>
                                        <StudentAssignPreview/>
                                    </Card>
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