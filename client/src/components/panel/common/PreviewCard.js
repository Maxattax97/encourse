import React, { Component } from 'react'
import { Card } from '../../Helpers'

class PreviewCard extends Component {
	render() {
		return (
			<Card onClick={ this.props.onClick } className={ this.props.isSelected ? 'action selected' : 'action' }>
				<div className="summary-preview">
					{ this.props.children }
				</div>
			</Card>
		)
	}
}

export default PreviewCard