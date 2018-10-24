import React, { Component } from 'react'

import {Card, Title, BackIcon} from '../Helpers'

class TANavigation extends Component {

    changeTA = (index) => {
        this.props.change(index)
    }

    render() {
        return (
            <div className="ta-nav-container">
                <div className="panel-left-nav">
                    <div className="list-nav side-nav-left">
                        <div className={ `top-nav back-nav svg-icon float-height${ this.props.backClick ? ' action' : '' }` } onClick={ this.props.backClick }>
                            <h3>
                                { this.props.back }
                            </h3>
                            {
                                this.props.backClick ? <BackIcon /> : null
                            }
                        </div>
                        <Card>
                            { !this.props.isLoading
                            ? <div className="list-container">
                                <Title header={ <h3 className='header'>Teaching Assistants</h3> }/>
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
                            : <div>{/* TODO: add spinner */}Loading</div>}
                            }
                        </Card>
                    </div>
                </div>
            </div>
        )
    }
}

export default TANavigation