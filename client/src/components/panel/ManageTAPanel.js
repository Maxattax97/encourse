import React, { Component } from 'react'

import ActionNavigation from '../navigation/ActionNavigation'
import TANavigation from '../navigation/TANavigation'
import {Summary, Title} from '../Helpers'

class ManageTAPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            teaching_assistants: [],
            current_ta: -1
        }
    }

    back = () => {

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
                        () => {},
                        () => {},
                        () => {},
                        () => {}
                    ]}
                    action_names={[
                        'Add New Teaching Assistant',
                        'Save Changes',
                        'Revert Changes',
                        'Remove Teaching Assistant'
                    ]} />
            </div>

            <div className="panel-center-content">
                <Title header={ <h1 className='header'>CS252 - Teaching Assistants</h1> } />
                <div className='h1 break-line header' />

                <Summary
                    header={
                        <h3 className='header'>
                            {this.state.teaching_assistants[this.state.current_ta] ? `${this.props.teaching_assistants[this.props.current_ta].name} - ` : 'New - '}Properties
                        </h3>
                    }
                    columns={ 2 }
                    data={ [ 1, 2 ] }
                    iterator={ (index) =>
                        index === 1 ?
                            <div key={index}>
                                <h4 className="header">
                                         Name
                                </h4>
                                <input type="text" className="h3-size" value={this.state.name} placeholder="Ex. Gerald, Amitha, ..." onChange={this.onChange} name="name" ref="name" autoComplete="off"/>
                                <h4 className="header">
                                         Purdue Career Account
                                </h4>
                                <input type="text" className="h3-size" value={this.state.source_name} placeholder="Ex. kleclain, montgo38, ..." onChange={this.onChange} name="source_name" ref="account_name" autoComplete="off"/>
                                <h4 className="header">
                                    Student Assignment Type
                                </h4>
                                <label className="h3-size">
                                    <input type="radio" name="assignment_type" />
                                    <h5 className="header">Assign students by career account</h5>
                                </label>
                                <label className="h3-size">
                                    <input type="radio" name="assignment_type" />
                                    <h5 className="header">Assign students by student card</h5>
                                </label>
                                <label className="h3-size">
                                    <input type="radio" name="assignment_type" />
                                    <h5 className="header">Assign all students</h5>
                                </label>
                            </div> :

                            null
                    } />


                <div className='h2 break-line' />
                <h3 className='header'>Students</h3>
            </div>

        </div>)
    }
}

export default ManageTAPanel