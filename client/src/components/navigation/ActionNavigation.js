import React, { Component } from 'react'
import { connect } from 'react-redux'

import { toggleHidden } from '../../redux/actions'
import {Title, Card} from '../Helpers'

class ActionNavigation extends Component {

    constructor(props) {
        super(props)

        this.state = {
            view_hidden_tests: false
        }
    }

    swapTestView = () => {
        this.setState({ view_hidden_tests: !this.state.view_hidden_tests },
            () => this.props.toggleHidden())
    }

    render() {
        return (
            <Card component={
                <div className="actions-container">
                    <Title header={ <h3 className='header'>Actions</h3> } />
                    <div className="h3 break-line header" />
                    <div className="text-list">
                        <div className="action">
                            <h4 onClick={ this.swapTestView }>
                                { this.state.view_hidden_tests ? 'View Visible Tests' : 'View Hidden Tests' }
                            </h4>
                        </div>
                        <div className="action">
                            <h4>
                                Run Tests
                            </h4>
                        </div>
                        <div className="action">
                            <h4>
                                Run Hidden Tests
                            </h4>
                        </div>
                        <div className="action">
                            <h4>
                                Pull Repository
                            </h4>
                        </div>
                        <div className="action">
                            <h4>
                                View Plagiarism
                            </h4>
                            <h4>
                                Report
                            </h4>
                        </div>
                    </div>
                </div>
            } />
        )
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleHidden: () => dispatch(toggleHidden()),
    }
}

export default connect(null, mapDispatchToProps)(ActionNavigation)