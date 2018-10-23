import React, { Component } from 'react'
import { connect } from 'react-redux'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import CommitHistory from './util/CommitHistory'

class ManageTAPanel extends React.Component {
    render() {
        return (<div className="manage-ta-panel">
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

        </div>)
    }
}

export default ManageTAPanel