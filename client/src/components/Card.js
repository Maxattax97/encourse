import React, { Component } from 'react';
import '../css/Card.css'

class Card extends Component {
    render() {
        return (
            <div className="Card">
                {this.props.component}
            </div>
        );
    }
}

export default Card;
