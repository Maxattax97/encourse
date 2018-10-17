import React, {Component} from 'react'
import ProjectNavigation from '../navigation/ProjectNavigation'
import ActionNavigation from '../navigation/ActionNavigation'
import {Card, Summary, Title} from '../Helpers'
import {history} from '../../redux/store'
import ClassProgressHistogram from '../chart/ClassProgressHistogram'
import ClassTestCasePercentDone from '../chart/ClassTestCasePercentDone'

class ProjectPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            name: '',
            source_name: '',
            created_date: '',
            due_date: ''
        }
    }

    back = () => {
        history.push('/course')
    };

    render() {
        return (
            <div className='panel-projects'>
                <ProjectNavigation onModalBlur={(blur) => this.setState({modal_blur : blur ? ' blur' : ''})}
                    back="Course"
                    backClick={ this.back }
                    {...this.props}/>

                <div className='panel-right-nav'>
                    <div className='top-nav' />
                    <ActionNavigation />
                </div>

                <div className='panel-center-content'>
                    <Title header={ <h1 className='header'>CS252 - Projects</h1> } />
                    <div className='h1 break-line header' />

                    <Summary header={ <h3 className='header'>this.props.currentProjectID</h3> }
                        columns={ 2 }
                        data={ [ 1, 2 ] }
                        iterator={ (index) => {
                            return index === 1 ?
                                <div>
                                    <h4 className="header">
                                        Name
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.name} onChange={this.onChange} name="name" ref="name" autoComplete="off"/>
                                    <h4 className="header">
                                        Source Name
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.source_name} placeholder="Ex. lab1-src, lab2, ..." onChange={this.onChange} name="source_name" ref="source_name" autoComplete="off"/>
                                    <h4 className="header">
                                        Created Date
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.created_date} placeholder="MM/DD/YYYY" onChange={this.onChange} name="created_date" ref="created_date" autoComplete="off"/>
                                    <h4 className="header">
                                        Due Date
                                    </h4>
                                    <input type="text" className="h3-size" value={this.state.due_date} placeholder="MM/DD/YYYY" onChange={this.onChange} name="due_date" ref="due_date" autoComplete="off"/>
                                </div> :
                                null
                        } }/>

                    <div className='h2 break-line' />
                    <h3 className='header'>Test Scripts</h3>
                    <table>
                        <thead>
                            <tr>
                                <td><h4 className='header'>#</h4></td>
                                <td><h4 className='header'>Name</h4></td>
                                <td><h4 className='header'>Point Value</h4></td>
                                <td><h4 className='header'>View Script</h4></td>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>1</td>
                                <td>Test Script 1</td>
                                <td>50</td>
                                <td>10</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        )
    }

}

export default ProjectPanel