import React, { Component } from 'react'

import {LoadingIcon, Card} from '../../Helpers'

class Statistics extends Component {

	render() {
		return (
			<div className='summary'>
				<div className='summary-container'>
                    <Card>
                        {
                            !this.props.stats.loading ?
                                this.props.children
                                : null
                        }
                        {
                            this.props.stats && this.props.stats.data && (!this.props.stats.loading || this.props.stats.data.length) ?
                                this.props.stats.data.map((value)  => 
                                    <div key={value.stat_name} className={'stat float-height' + (value.break ? ' break' : '')} title={ value.stat_desc ? value.stat_desc : null }>
                                        <h5>{value.stat_name}</h5>
                                        <h5>{value.stat_value ? (value.stat_value.toFixed ? value.stat_value.toFixed(2) : value.stat_value) : ''}</h5>
                                    </div>
                                )
                                :
                                <div className='loading'>
                                    <LoadingIcon/>
                                </div>
                        }
                    </Card>
				</div>
			</div>
		)
	}
}

export default Statistics
