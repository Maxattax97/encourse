import React, { Component } from 'react'
import ActionNavigation from '../navigation/ActionNavigation'
import {history} from '../../redux/store'
import {BackNav, Card, SettingsIcon, Summary, Title} from '../Helpers'
import CourseDishonestyModal from '../modal/CourseDishonestyModal'
import {getStudentPreviews, setCurrentStudent, setModalState} from '../../redux/actions'
import connect from 'react-redux/es/connect/connect'
import StudentReportFilter from './course-dishonesty/StudentReportFilter'
import CourseDishonestyCharts from "./course-dishonesty/CourseDishonestyCharts"

class CourseDishonestyPanel extends Component {

    back = () => {
        history.push('/course')
    }

    scrolledToBottom = () => {

    }

    render() {

        const action_names = [
            'Sync Repositories',
            'Run Tests',
            'Share Results'
        ]

        const actions = [
            () => {  },
            () => {  },
            () => {  }
        ]

        return (<div className="class-dishonesty-panel">

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

            <CourseDishonestyModal id={1}/>

            <div className='panel-center-content'>

                <div className='panel-course-report'>
                    <Title onClick={ () => this.props.setModalState(1) }>
                        <h1 className='header'>CS252 - Academic Dishonesty Report</h1>
                        <SettingsIcon/>
                    </Title>
                    <div className='h1 break-line header' />

                    <h3 className='header'>Course Charts Summary</h3>
                    <CourseDishonestyCharts/>

                    <div className='h1 break-line' />

                    <h3 className='header'>Students Summary</h3>
                    <StudentReportFilter/>
                </div>
            </div>
        </div> )
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : []
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CourseDishonestyPanel)