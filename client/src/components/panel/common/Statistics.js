import React, { Component } from 'react'
import { connect } from 'react-redux'

import { getClassStatistics } from '../../../redux/actions/index'
import url from '../../../server'
import {LoadingIcon, Card} from '../../Helpers'

class Statistics extends Component {

	render() {
		return (
			<div className='summary'>
				<div className='summary-container'>
					<div className='float-height cols-2'>
						<Card>
							{
								!this.props.isLoading && this.props.values && this.props.values.map ?
									this.props.values.map((value)  =>
										<div key={value.stat_name} className={'stat float-height' + (value.break ? ' break' : '')}>
											<h5>{value.stat_name}</h5>
											<h5>{value.stat_value}</h5>
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
			</div>
		)
	}
}

export default Statistics
