import React, { Component } from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'

class StudentDishonestyPanel extends Component {
    render() {
        return (<div className="student-dishonesty-panel">
            <ProjectNavigation
                info={ this.props.projects }
                back="Course"
                backClick={ this.back }
                onModalBlur={ (blur) => this.setState({modal_blur : blur ? ' blur' : ''}) }
                { ...this.props }/>

            <div className="panel-right-nav">
                <div className='top-nav' />
                <ActionNavigation actions={[
                    () => {},
                    () => {},
                    () => {}
                ]}
                action_names={[
                    'View Hidden Tests',
                    'Run Tests',
                    'Academic Dishonesty Report'
                ]} />
            </div>
        </div>)
    }
}

export default StudentDishonestyPanel