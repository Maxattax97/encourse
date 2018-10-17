import React, { Component } from 'react'
import { connect } from 'react-redux'

import { toggleHidden } from '../../redux/actions'
import {Title, Card} from '../Helpers'

class ActionNavigation extends Component {

    render() {
        return (
            <div className='actions-container side-nav-right'>
                <Card component={
                    <div className='actions'>
                        <Title header={ <h3 className='header'>Actions</h3> } />
                        <div className="h3 break-line header" />
                        <div className="text-list">
                            <div className="action">
                                <h4 onClick={this.props.toggleHidden}>
                                    { this.props.isHidden ? 'View Visible Tests' : 'View Hidden Tests' }
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
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
       isHidden: state.projects.isHidden
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleHidden: () => dispatch(toggleHidden()),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ActionNavigation)