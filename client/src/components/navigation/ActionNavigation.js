import React, { Component } from 'react'
import { connect } from 'react-redux'

import { toggleHidden } from '../../redux/actions'
import {Card} from '../Helpers'

class ActionNavigation extends Component {

    action_names_default = ['View Hidden Tests', 'Run Tests', 'Run Hidden Tests', 'Pull Repository', 'View Plagiarism Report']

    render() {
        return (
            <div className='actions-container side-nav-right'>
                <Card>
                    <div className='actions'>
                        <h3 className='header'>Actions</h3>
                        <div className="h3 break-line header" />
                        <div className="text-list">
                            {
                                this.props.action_names ?
                                    this.props.action_names.map((name, index) =>
                                        <div className='action' key={index}>
                                            <h4 onClick={ this.props.actions[index] }>
                                                {name}
                                            </h4>
                                        </div>
                                    ) :
                                    this.props.actions.map((action, index) =>
                                        <div className='action' key={index}>
                                            <h4 onClick={ this.props.actions[index] }>
                                                {this.action_names_default[index]}
                                            </h4>
                                        </div>
                                    )
                            }
                        </div>
                    </div>
                </Card>
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