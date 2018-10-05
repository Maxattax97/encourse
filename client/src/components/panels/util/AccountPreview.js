import React, { Component } from 'react';
import { connect } from 'react-redux'

import { removeAccount } from '../../../redux/actions'
import url from '../../../server'


class AccountPreview extends Component {

    render() {
        return (
            <div className="account-preview" >
                <div className="title">
                    <h3 onClick={this.deleteAccount}>{this.props.account.userName}</h3>
                    <h3>{this.props.getRole(this.props.account.role)}</h3>
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
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(AccountPreview);
