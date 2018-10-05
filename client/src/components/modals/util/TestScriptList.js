import React, { Component } from 'react'

import searchIcon from "../../../img/search.svg";
import plusIcon from "../../../img/plus.svg";
import deleteIcon from "../../../img/delete.svg";

class TestScriptList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            scripts: [],
            index: 0,
            pointValue: [],
        };
    }

    static getDerivedStateFromProps(props, state) {
        //console.log(props, state);
        if(props.script_list && props.script_list.map)
            return {
                scripts: props.script_list
            };

        return {
            scripts: state.scripts ? state.scripts : []
        };
    }

    addTestScript = () => {
        this.state.scripts.push({ testScriptName: "", pointvalue: 5, file: null});
        this.setState({ scripts : this.state.scripts });
    }

    removeTestScript = (script_index) => {
        this.state.scripts.splice(script_index, 1)
        this.setState({ scripts : this.state.scripts })
    }

    uploadFile = (ev) => {
        let file = ev.target.files[0]
        console.log(file)
        if(!file) return
        console.log(file)
        this.state.scripts[this.state.index].testScriptName = file.name
        this.state.scripts[this.state.index].file = file
        this.setState({ scripts : this.state.scripts })
    }   

    updatePointValue = (ev, script_index) => {
        console.log(ev.target.value)
        this.state.scripts[script_index].pointValue = ev.target.value
        this.state.pointValue[script_index] = ev.target.value
        this.setState({ scripts : this.state.scripts, pointValue: this.state.pointValue })
    }

    render() {
        return (
                <div className="test-scripts-list">
                    {
                        this.state.scripts.map((script, index) =>
                            <div className="scripts-script" key={`${ Date.now()+index }`}>
                                <input type="file" id="script-upload" onChange={(ev) => {console.log('here');this.uploadFile(ev)}} ref={ index } style={{display: "none"}}/>
                                <div className="script-filename">
                                    <img src={ searchIcon } onClick={ () => this.setState({ index }, () => document.getElementById('script-upload').click())}/>
                                    <h4>
                                        Test { index + 1 }
                                    </h4>
                                </div>
                                <div className="script-delete" onClick={ () => this.removeTestScript(index) }>
                                    <img src={deleteIcon} />
                                </div>
                                <input type="number" value={this.state.pointValue[index] ? this.state.pointValue[index] : 5 } onChange={(ev) => this.updatePointValue(ev, index)} step="1" />
                            </div>
                        )
                    }
                    <div className="scripts-new" onClick={ this.addTestScript }>
                        <img src={ plusIcon } />
                    </div>
                </div>
        )
    }
}

export default TestScriptList