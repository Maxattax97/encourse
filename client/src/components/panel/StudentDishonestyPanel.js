import React, { Component } from 'react'
import { connect } from 'react-redux'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav, Card, SettingsIcon, Summary, Title} from '../Helpers'
import {history} from '../../redux/store'
import ProgressPerTime from './student/chart/StudentVelocityPerTime'
import ProgressPerCommit from './student/chart/StudentVelocityPerCommit'
import {clearStudent, getStudent} from '../../redux/actions'

class StudentDishonestyPanel extends Component {

    back = () => {
        history.push('/course')
    }

    render() {

        const chartList = [
            <ProgressPerTime key={1}/>,
            <ProgressPerCommit key={2}/>,
        ]

        const action_names = [
            'Sync Repository',
            'Run Tests',
            'Share Results'
        ]

        const actions = [
            () => {  },
            () => {  },
            () => {  }
        ]

        return (
            <div className="student-dishonesty-panel">
                <div className='panel-left-nav'>
                    <BackNav back='Course' backClick={ this.back }/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <div className='panel-right-nav'>
                    <div className='top-nav'>
                        <div>
                            <h4>Last Sync:</h4>
                        </div>
                        <div>
                            <h4>Last Test Ran:</h4>
                        </div>
                    </div>
                </div>

                <div className='panel-center-content'>

                    <div className='panel-student-report'>
                        <h1 className='header'>CS252 - { this.props.currentStudent.first_name } { this.props.currentStudent.last_name } - Academic Dishonesty Report</h1>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Student Charts Summary</h3>
                        <Summary
                            columns={ 2 }
                            data={ chartList }
                            className='charts'
                            iterator={ (chart) => <Card key={ chart.key }>
                                { chart }
                            </Card> } >
                        </Summary>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentDishonestyPanel)