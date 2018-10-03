import React, { Component } from 'react'

import searchIcon from "../../img/search.svg";
import plusIcon from "../../img/plus.svg";

class TestScriptList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            scripts: []
        };
    }

    static getDerivedStateFromProps(props, state) {
        if(props.script_list && props.script_list.map)
            return {
                scripts: props.script_list
            };

        return {
            scripts: []
        };
    }

    addTestScript = () => {
        this.state.scripts.push({ filename: "", pointvalue: 0 });

        this.setState({ scripts : this.state.scripts });
    };

    render() {
        return (
                <div className="test-scripts-list">
                    {
                        this.state.scripts.map((script) =>
                            <div className="scripts-script" key={`${ script.filename }-${ script.pointvalue }`}>
                                <div className="script-filename">
                                    <img src={ searchIcon } />
                                    <h4>
                                        { script.filename }
                                    </h4>
                                </div>
                                <input type="number" placeholder="5" step="1" />
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