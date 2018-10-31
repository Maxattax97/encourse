import React, { Component } from 'react'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav} from '../Helpers'

class StudentDishonestyPanel extends Component {
    render() {

        const action_names = [
            'Sync Repository',
            'Run Tests',
            'Share Results'
        ]

        const actions = [
            () => {  },
            () => {  },
            () => {  }
        ]

        return (
            <div className="student-dishonesty-panel">
                <div className='panel-left-nav'>
                    <BackNav back='Course' backClick={ this.back }/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className='panel-right-nav'>
                    <div className='top-nav'>
                        <div className='course-repository-info'>
                            <h4>Last Sync:</h4>
                            <h4>Last Test Ran:</h4>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default StudentDishonestyPanel