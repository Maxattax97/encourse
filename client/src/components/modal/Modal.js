import React, { Component } from 'react'

import {Card, XIcon} from '../Helpers'

class Modal extends Component {

    render() {
        return (
            <div className='modal' style={this.props.show ? {} : {display: 'none'}} onClick={ this.props.onExit }>
                <div className={this.props.left ? 'modal-left' : this.props.right ? 'modal-right' : 'modal-center'} onClick={(e) => e.stopPropagation()}>
                    <Card>
                        <div className={'modal-container'} key={1}>
                            {this.props.children}
                        </div>
                        <div className="action svg-icon exit-nav" onClick={ this.props.onClose || this.props.onExit } key={2}>
                            <XIcon/>
                        </div>
                    </Card>
                </div>
            </div>
        )
    }
}

export default Modal