import React, { Component } from 'react'

import exitIcon from '../../img/x.svg'
import Card from "../Card";

class Modal extends Component {

    render() {
        return (
            <div className={"modal"} style={this.props.show ? {} : {display: "none"}} onClick={ this.props.onExit }>
                <div className={this.props.left ? "modal-left" : this.props.right ? "modal-right" : "modal-center"} onClick={(e) => e.stopPropagation()}>
                    <Card component={<div className={"modal-container"}>
                        {this.props.component}
                        <div className="exit-nav" onClick={ this.props.onClose || this.props.onExit }>
                            <img src={exitIcon} alt="" />
                        </div>
                    </div>} />
                </div>
            </div>
        )
    }
}

export default Modal