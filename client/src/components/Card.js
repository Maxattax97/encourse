import React, { Component } from 'react';

class Card extends Component {
    render() {
        return (
            <div className="card">
                <div className="component">
                    {this.props.component}
                </div>
            </div>
        );
    }
}

export default Card;
