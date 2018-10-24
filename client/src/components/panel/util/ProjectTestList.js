import React, { Component } from 'react'

import {PlusIcon, SearchIcon, Title, TrashIcon} from '../../Helpers'

class ProjectTestList extends Component {

    constructor(props) {
        super(props)

        this.state = {
            scripts: [],
            index: 0,
            pointValue: [],
        }
    }

    static getDerivedStateFromProps(props, state) {
        //console.log(props, state);
        if(props.script_list && props.script_list.map)
            return {
                scripts: props.script_list
            }

        return {
            scripts: state.scripts ? state.scripts : []
        }
    }

    addTestScript = () => {
        this.state.scripts.push({ testScriptName: '', pointvalue: 5, file: null})
        this.setState({ scripts : this.state.scripts })
    }

    removeTestScript = (script_index) => {
        this.state.scripts.splice(script_index, 1)
        this.setState({ scripts : this.state.scripts })
    }

    uploadFile = (ev) => {
        let file = ev.target.files[0]
        if(!file) return
        this.state.scripts[this.state.index].testScriptName = file.name
        this.state.scripts[this.state.index].file = file
        this.setState({ scripts : this.state.scripts })
    }

    updatePointValue = (ev, script_index) => {
        this.state.scripts[script_index].pointValue = ev.target.value
        this.state.pointValue[script_index] = ev.target.value
        this.setState({ scripts : this.state.scripts, pointValue: this.state.pointValue })
    }

    render() {
        return (
            <div className="project-test-list">
                <Title header={ <h3 className='header'>${this.props.header}</h3> } />
                <table>
                    <thead>
                        <tr>
                            <td><h4 className='header'>#</h4></td>
                            <td><h4 className='header'>Name</h4></td>
                            <td><h4 className='header'>Point Value</h4></td>
                            <td><h4 className='header'>Actions</h4></td>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            this.state.scripts.map((script, index) =>
                                <tr key={index}>
                                    <td><h4 className='header'>${index}</h4></td>
                                    <td><h4 className='header'>${script.name}</h4></td>
                                    <td><h4 className='header'>${script.pointValue}</h4></td>
                                    <td>
                                        <div className='test-actions'>
                                            <input type="file" onChange={(ev) => this.uploadFile(ev)} ref={ index } style={{display: 'none'}}/>
                                            <div className='svg-icon action'  onClick={ () => this.setState({ index }, () => document.getElementById('script-upload').click()) }>
                                                <SearchIcon/>
                                            </div>
                                            <div className='svg-icon action'  onClick={ () => this.removeTestScript(index) }>
                                                <TrashIcon/>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
                <div className="scripts-new action svg-icon" onClick={ this.addTestScript }>
                    <PlusIcon/>
                </div>
            </div>
        )
    }
}

export default ProjectTestList