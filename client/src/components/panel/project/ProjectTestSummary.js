import React, { Component } from 'react'
import { connect } from 'react-redux'
import {getCurrentProject, getTestScripts} from '../../../redux/state-peekers/projects'
import {retrieveTestScripts} from '../../../redux/retrievals/projects'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {Title} from '../../Helpers'
import {setModalState} from '../../../redux/actions'

class ProjectTestSummary extends Component {

	componentDidMount() {
		if(this.props.project)
			retrieveTestScripts(this.props.project)
	}

	componentDidUpdate(prevProps) {
		if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
			retrieveTestScripts(this.props.project)
	}

	clickTestScriptCard = (testscript) => {
        this.props.setModalState(4)
    }

	renderPreview = (testscript) => {
	    return (
            <div>
                <Title>
                    <h4>{ testscript.id }</h4>
                </Title>
                <div className="h4 break-line header" />
                <div className="preview-content">
                    <h5>Points: { testscript.points }</h5>
                    <h5>Visibility: { testscript.hidden ? 'Hidden' : 'Visible' }</h5>
                    <h5>Test Suites: { testscript.suites.map((id, index) => (id + (index === testscript.suites.length - 1 ? '' : ', '))) }</h5>
                </div>
            </div>
        )
    }
	
	render() {
		return (
		    <SelectableCardSummary
                type='testscripts'
                values={this.props.testScripts}
                render={this.renderPreview}
                onClick={this.clickTestScriptCard}
            />
		)
	}
}

const mapStateToProps = (state) => {
	return {
		project: getCurrentProject(state),
		testScripts: getTestScripts(state),
	}
}

const mapDispatchToProps = (dispatch) => {
    return {
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectTestSummary)