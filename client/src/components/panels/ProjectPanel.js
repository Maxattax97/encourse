import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'

class ProjectPanel extends Component {

    render() {
        return (
            <div className='panel-projects'>
                <ProjectNavigation onModalBlur={(blur) => this.setState({modal_blur : blur ? ' blur' : ''})}
                    {...this.props}/>

                <div className='panel-right-nav'>
                    <div className='top-nav' />
                    <ActionNavigation />
                </div>

                <div className='panel-center-content'>
                </div>
            </div>
        )
    }

}

export default ProjectPanel