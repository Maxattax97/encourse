import React, { Component } from 'react'

import {Card, Title, BackIcon} from '../Helpers'

class TANavigation extends Component {

    render() {
        return (
            <div className="ta-nav-container">
                <div className="panel-left-nav">
                    <div className="list-nav side-nav-left">
                        <div className={ `top-nav back-nav svg-icon float-height${ this.props.backClick ? ' action' : '' }` } onClick={ this.props.backClick }>
                            <h3>
                                { this.props.back }
                            </h3>
                            {
                                this.props.backClick ? <BackIcon /> : null
                            }
                        </div>
                        <Card>
                            <div className="list-container">
                                <Title header={ <h3 className='header'>Teaching Assistants</h3> }/>
                                <div className="h3 break-line header"/>
                                <div className='text-list'>
                                    {
                                        this.props.teaching_assistants &&
                                        this.props.teaching_assistants.map((project, index) =>
                                            <div key={ project.id }
                                                onClick={ () => this.changeProject(project.id, index) }
                                                className={ `action${this.props.currentProjectIndex === index && !this.state.new_project ? ' list-highlight' : ''}` }>
                                                <h4>
                                                    { project.project_name }
                                                </h4>
                                            </div>)
                                    }
                                </div>
                            </div>
                        </Card>
                    </div>
                </div>
            </div>
        )
    }
}

export default TANavigation