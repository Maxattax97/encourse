import React, { Component } from 'react'
import {Card, LoadingIcon} from '../../Helpers'

class CommitHistory extends Component {

    setOverflowRef = (node) => {
        if(this.overflow)
            this.overflow.removeEventListener('scroll', this.scrolledToBottom)

        this.overflow = node

        if(this.overflow)
            this.overflow.addEventListener('scroll', this.scrolledToBottom)
    }

    scrolledToBottom = (e) => {
        if(e.currentTarget.scrollHeight - e.currentTarget.scrollTop <= e.currentTarget.clientHeight+1 && this.props.onPaginate)
            this.props.onPaginate()
    }

    componentWillUnmount() {
        if(this.overflow)
            this.overflow.removeEventListener('scroll', this.scrolledToBottom)
    }

    render() {
        return (
            <div className='commits-container side-nav-right'>
                <Card>
                    <div className='commits'>
                        <h3 className='header'>History</h3>
                        <div className="h3 break-line header" />
                        {
                            !this.props.isLoading && this.props.data ?
                                <div className="float-height overflow" ref={ this.setOverflowRef }>
                                    {
                                        this.props.data.map((commit) =>
                                            <Card key={ commit.date } onClick={this.props.onClick ? () => this.props.onClick(commit) : null} className={ this.props.onClick ? 'action' : null }>
                                                <div className="student-commit-container">
                                                    <div>
                                                        <h5>
                                                            { commit.date }
                                                        </h5>
                                                    </div>
                                                    <div className='h5 break-line header' />
                                                    <ul>
                                                        {
                                                            commit.files.map((file) =>
                                                                <li key={file}>
                                                                    { file }
                                                                </li>
                                                            )
                                                        }
                                                    </ul>
                                                </div>
                                            </Card>
                                        )
                                    }
                                </div>
                                :
                                <div className='loading'>
                                    <LoadingIcon/>
                                </div>
                        }
                    </div>
                </Card>
            </div>
        )
    }
}

export default CommitHistory