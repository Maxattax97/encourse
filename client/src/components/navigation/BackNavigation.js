import {Component} from 'react'
import React from 'react'
import {connect} from 'react-redux'

class BackNavigation extends Component {
    render() {
        return (
            <div className='top-nav back-nav svg-icon float-height action'>
                <h3>
                    { this.props.back }
                </h3>
            </div>
        )
    }
}

export default connect()(BackNavigation)