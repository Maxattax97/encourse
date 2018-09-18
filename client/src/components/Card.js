import React, { Component } from 'react';
import logo from '../img/encourse-logo-large.png';

class Card extends Component {
    render() {
        return (
            <div className="Login">
                <header className="Login-header">
                    <img src={logo} className="EnCourse-logo" alt="logo" />
                </header>
                <div className="credentials-container">
                    <form>
                        <p>Username</p>
                        <input type="text" />
                        <p>Password</p>
                        <input type="text" />
                        <br />
                        <input type="submit" value="Enter Credentials" />
                    </form>
                </div>
            </div>
        );
    }
}

export default Card;
