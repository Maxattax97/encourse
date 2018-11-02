import React, { Component } from 'react'
import { connect } from 'react-redux'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav} from '../Helpers'
import {history} from '../../redux/store'
import {clearStudent, getStudent} from '../../redux/actions'
import url from '../../server'
import SyncItem from './common/SyncItem'
import StudentDishonestyCharts from "./student-dishonesty/StudentDishonestyCharts"

class StudentDishonestyPanel extends Component {

    componentDidMount = () => {
        if(!this.props.currentStudent) {
            this.props.getStudent(`${url}/api/studentsData?courseID=cs252&semester=Fall2018&userName=${this.props.match.params.id}`)
        }
    }

    back = () => {
        history.goBack()
    }

    render() {

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
                    <SyncItem />
                </div>

                <div className='panel-center-content'>

                    <div className='panel-student-report'>
                        <h1 className='header'>CS252 - { this.props.currentStudent ? this.props.currentStudent.first_name : '' } { this.props.currentStudent ? this.props.currentStudent.last_name : '' } - Academic Dishonesty Report</h1>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Student Charts Summary</h3>
                        <StudentDishonestyCharts/>
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

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentDishonestyPanel)