import React, { Component } from 'react'

import searchIcon from "../../../img/search.svg";
import plusIcon from "../../../img/plus.svg";
import deleteIcon from "../../../img/delete.svg";

class TestScriptList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            scripts: []
        };
    }

    static getDerivedStateFromProps(props, state) {
        console.log(props, state);
        if(props.script_list && props.script_list.map)
            return {
                scripts: props.script_list
            };

        return {
            scripts: state.scripts ? state.scripts : []
        };
    }

    addTestScript = () => {
        this.state.scripts.push({ filename: "", pointvalue: 0 });

        this.setState({ scripts : this.state.scripts });
    };

    removeTestScript = (script_index) => {
        this.state.scripts.splice(script_index, 1);

        this.setState({ scripts: this.state.scripts });
    };

    render() {
        return (
                <div className="test-scripts-list">
                    {
                        this.state.scripts.map((script, index) =>
                            <div className="scripts-script" key={`${ index }`}>
                                <input type="file" ref={ index } style={{display: "none"}}/>
                                <div className="script-filename">
                                    <img src={ searchIcon } />
                                    <h4>
                                        Test { index + 1 }
                                    </h4>
                                </div>
                                <div className="script-delete" onClick={ () => this.removeTestScript(index) }>
                                    <img src={deleteIcon} />
                                </div>
                                <input type="number" defaultValue="5" step="1" />
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