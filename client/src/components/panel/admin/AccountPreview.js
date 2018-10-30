import React, { Component } from 'react'
import { connect } from 'react-redux'

// import { removeAccount } from '../../../redux/actions'
// import url from '../../../server'


class AccountPreview extends Component {

    render() {
        console.log(this.props.account)
        return (
            <div className="account-preview" >
                <div className="title">
                    <h3>{this.props.account.userName}</h3>
                    <h3>{this.props.getRole(this.props.account.role)}</h3>
                </div>
                <h4 className="break-line title" />
            </div>
        )
    }
}

const mapDispatchToProps = (/* dispatch */) => {
    return {
    }
}

export default connect(null, mapDispatchToProps)(AccountPreview)
