import {Component} from 'react'
import React from 'react'
import {connect} from 'react-redux'

class BackNavigation extends Component {
    render() {
        return (
            <div className={ `top-nav back-nav svg-icon float-height${ this.props.backClick ? ' action' : '' }` } onClick={ this.props.backClick }>
                <h3>
                    { this.props.back }
                </h3>
            </div>
        )
    }
}

export default connect()(BackNavigation)