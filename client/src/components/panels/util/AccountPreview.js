import React, { Component } from 'react';

class AccountPreview extends Component {
    render() {
        return (
            <div className="account-preview">
                <div className="title">
                    <h3>{this.props.account.login}</h3>
                    <h3>{this.props.account.account_type}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        );
    }
}

export default AccountPreview;
