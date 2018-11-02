import React, { Component } from 'react'
import { connect } from 'react-redux'
import ActionNavigation from '../navigation/ActionNavigation'
import {BackNav} from '../Helpers'
import {history} from '../../redux/store'
import {clearStudent, getStudent, syncStudentRepository, runStudentTests} from '../../redux/actions'
import url from '../../server'
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
            () => { 
                if(this.props.currentProjectId && this.props.currentStudent)
                    this.props.syncStudentRepository(`${url}/api/pull/project?projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            },
            () => {
                if(this.props.currentProjectId && this.props.currentStudent)
                    this.props.runStudentTests(`${url}/api/run/testall?projectID=${this.props.currentProjectId}&userName=${this.props.currentStudent.id}`)
            },
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
        currentStudent: state.student && state.student.currentStudent !== undefined ? state.student.currentStudent : undefined,
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudent: (url, headers, body) => dispatch(getStudent(url, headers, body)),
        syncStudentRepository: (url, headers, body) => dispatch(syncStudentRepository(url, headers, body)),
        runStudentTests: (url, headers, body) => dispatch(runStudentTests(url, headers, body)),
        clearStudent: () => dispatch(clearStudent),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(StudentDishonestyPanel)