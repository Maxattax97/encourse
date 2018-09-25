import React, { Component } from 'react'

import exitIcon from '../img/x.svg'
import Card from "./Card";

class Modal extends Component {

    render() {
        return (
            <div className={"modal"} onClick={this.props.onClose}>
                <div className={this.props.left ? "modal-left" : this.props.right ? "modal-right" : "modal-center"} onClick={(e) => e.stopPropagation()}>
                    <Card component={<div className={"modal-container"}>
                        {this.props.component}
                        <div className="exit-nav" onClick={this.props.onClose}>
                            <img src={exitIcon} alt="" />
                        </div>
                    </div>} />
                </div>
            </div>
        )
    }
}

export default Modal