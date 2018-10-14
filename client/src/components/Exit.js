import React, { Component } from 'react'

import exitIcon from '../resources/x.svg'

class Exit extends Component {

    render() {
        return (
            <div className="panel-right-nav">
                <div className="exit-nav" onClick={this.props.onClick}>
                    <img src={exitIcon} />
                </div>
            </div>
        )
    }
}

export default Exit