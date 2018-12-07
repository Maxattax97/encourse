import React, { Component } from 'react'
import { connect } from 'react-redux'
import {getCurrentProject, getTestSuites} from '../../../redux/state-peekers/projects'
import SelectableCardSummary from '../common/SelectableCardSummary'
import {Title} from '../../Helpers'
import {setModalState} from '../../../redux/actions'
import {retrieveTestSuites} from '../../../redux/retrievals/projects'

class ProjectSuiteSummary extends Component {

    componentDidMount() {
        if(this.props.project)
            retrieveTestSuites(this.props.project)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            retrieveTestSuites(this.props.project)
    }

    clickSuiteCard = () => {
        this.props.setModalState(4)
    }

    renderPreview = (suite) => {
        return (
            <div>
                <Title>
                    <h4>{ suite }</h4>
                </Title>
                <div className="h4 break-line header" />
            </div>
        )
    }

    render() {
        if(!this.props.project)
            return null

        return (
            <div className='project-test-suites'>
                <h3 className='header'>Test Suites</h3>
                <SelectableCardSummary
                    type='testscripts'
                    values={this.props.suites.data}
                    render={this.renderPreview}
                    onClick={this.clickSuiteCard}
                    noReset
                />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        project: getCurrentProject(state),
        suites: getTestSuites(state),
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectSuiteSummary)