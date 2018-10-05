import React, { Component } from 'react';
import { connect } from 'react-redux'

import { removeAccount } from '../../../redux/actions'
import url from '../../../server'


class AccountPreview extends Component {

    deleteAccount = () => {
        //TODO!: add parameter to delete any account
        this.props.removeAccount(`${url}/api/delete/user?userName=${this.props.account.login}`,
        {'Authorization': `Bearer ${this.props.token}`})
    }

    getRole = (role) => {
        switch(role) {
            case 0:
                return 'student'
            case 1:
                return 'TA'
            case 2:
                return 'professor'
            case 3:
                return 'admin'
        }
    }

    render() {
        return (
            <div className="account-preview" >
                <div className="title">
                    <h3 onClick={this.deleteAccount}>{this.props.account.userName}</h3>
                    <h3>{this.getRole(this.props.account.role)}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        removeAccount: (url, headers, body) => dispatch(removeAccount(url, headers, body)),
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(AccountPreview);
