import React, { Component } from 'react'

import {Card, XIcon} from '../Helpers'
import {setModalState} from '../../redux/actions'
import connect from 'react-redux/es/connect/connect'

class Modal extends Component {

    render() {
        return (
            <div className='modal' style={this.props.currentModal === this.props.id ? {} : {display: 'none'}} onClick={ this.props.closeModal }>
                <div className={this.props.left ? 'modal-left' : this.props.right ? 'modal-right' : 'modal-center'} onClick={(e) => e.stopPropagation()}>
                    <Card>
                        <div className={'modal-container'}>
                            {this.props.children}
                        </div>
                        <div className="action svg-icon exit-nav" onClick={ this.props.closeModal }>
                            <XIcon/>
                        </div>
                    </Card>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentModal: state.control && state.control.getCurrentModal ? state.control.getCurrentModal : 0,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        closeModal: () => dispatch(setModalState(0)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Modal)