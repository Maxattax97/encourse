import React, { Component } from 'react'

import {Card} from '../Helpers'

class TANavigation extends Component {

    changeTA = (index) => {
        this.props.change(index)
    }

    render() {
        return (
            <div className="list-nav side-nav-left">
                <Card>
                    { !this.props.isLoading
                        ? <div className="list-container">
                            <h3 className='header'>Teaching Assistants</h3>
                            <div className="h3 break-line header"/>
                            <div className='text-list'>
                                {
                                    this.props.teaching_assistants &&
                                        this.props.teaching_assistants.map((ta, index) =>
                                            <div key={ ta.id }
                                                onClick={ () => this.changeTA(index) }
                                                className={ `action${this.props.current_ta === index ? ' list-highlight' : ''}` }>
                                                <h4>
                                                    { `${ta.first_name} ${ta.last_name}` }
                                                </h4>
                                            </div>)
                                }
                            </div>
                        </div>
                        : <div>Loading</div>}
                </Card>
            </div>
        )
    }
}

export default TANavigation