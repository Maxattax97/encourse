import React, { Component } from 'react'

export class Title extends Component {
    render() {
        return (
            <div className={ `title${this.props.onClick ? ' action' : ''}` } onClick={ this.props.onClick || null }>
                { this.props.header }
                { this.props.icon ? <img className='svg-icon' src={ this.props.icon.icon } alt={ this.props.icon.alt_text } /> : null }
            </div>
        )
    }
}