import React, { Component } from 'react'
import ActionNavigation from '../navigation/ActionNavigation'
import {history} from '../../redux/store'
import {BackNav, SettingsIcon, Title} from '../Helpers'
import CourseDishonestyModal from '../modal/CourseDishonestyModal'
import {getStudentPreviews, setCurrentStudent, setModalState, getDishonestyReport} from '../../redux/actions'
import url from '../../server'
import connect from 'react-redux/es/connect/connect'
import StudentReportFilter from './course-dishonesty/StudentReportFilter'
import CourseDishonestyCharts from './course-dishonesty/CourseDishonestyCharts'

class CourseDishonestyPanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            filters : {
                sort_by: 0,
                order_by: 0
            }
        }
    }

    back = () => {
        history.goBack()
    }

    componentDidMount = () => {
        this.props.getDishonestyReport(`${url}/api/classCheating?projectID=${this.props.currentProjectId}`)
    }

    scrolledToBottom = () => {
        console.log('bottom')
    }

    changeFilter = (key, value) => {
        this.state.filters[key] = value

        this.setState({ filters: Object.assign({}, this.state.filters) })
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

        return (
            <div className="class-dishonesty-panel">

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
                        <StudentReportFilter onChange={ this.changeFilter } filters={ this.state.filters } report={this.props.report}/>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : [],
        report: state.course && state.course.getDishonestyReportData ? state.course.getDishonestyReportData.content : [],
        currentProjectId: state.projects ? state.projects.currentProjectId : null,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        getDishonestyReport: (url, headers, body) => dispatch(getDishonestyReport(url, headers, body)),
        setModalState: (id) => dispatch(setModalState(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CourseDishonestyPanel)