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

export class Summary extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className={ `summary ${this.props.className ? this.props.className : ''}` }>
                { this.props.header ? this.props.header : null }
                { this.props.data && this.props.data.map && this.props.iterator ?
                    <div className='summary-container'>
                        <div className={`float-height cols-${this.props.columns ? this.props.columns : '2'}`}>
                            { this.props.data.map(this.props.iterator) }
                        </div>
                    </div> : null }
            </div>
        )
    }
}

export class Card extends Component {
    render() {
        return (
            <div className={ `card${this.props.className ? ` ${this.props.className}` : '' }` } onClick={this.props.onClick || (() => {})}>
                <div className="component">
                    {this.props.component}
                </div>
            </div>
        )
    }
}
