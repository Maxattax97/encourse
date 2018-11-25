import React, { Component } from 'react'
import {Card, Summary} from '../../Helpers'

class ChartList extends Component {

	constructor(props, charts) {
		super(props)

		this.charts = charts
	}

	render() {
		return (
			<Summary columns={ 2 } className='charts'>
				{
					this.charts.map( (Chart, index) =>
						<Card key={ index }>
							<Chart />
						</Card>
					)
				}
			</Summary>
		)
	}
}

export default ChartList