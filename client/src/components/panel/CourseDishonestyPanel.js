import React, { Component } from 'react'
import ActionNavigation from '../navigation/ActionNavigation'
import {history} from '../../redux/store'
import {BackNav, SettingsIcon, Title} from '../Helpers'
import CourseDishonestyModal from '../modal/CourseDishonestyModal'
import {getStudentPreviews, setCurrentStudent, setModalState, getDishonestyReport, updateCourseDishonestyPage, resetCourseDishonestyPage} from '../../redux/actions'
import url from '../../server'
import connect from 'react-redux/es/connect/connect'
import StudentReportFilter from './course-dishonesty/StudentReportFilter'
import CourseDishonestyCharts from './course-dishonesty/CourseDishonestyCharts'
import SyncItem from './common/SyncItem'

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
        if(!this.props.last) {
            this.props.getDishonestyReport(`${url}/api/classCheating?projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy()}&page=${this.props.page + 1}`)
            this.props.updateCourseDishonestyPage()
        }
    }

    getSortBy = (value) => {
        let id = value ? value : this.state.filters.sort_by
        switch(id) {
            case 0:
                return 'id'
            case 1:
                return 'score' 
        }
    }

    changeFilter = (key, value) => {
        this.state.filters[key] = value

        if(key === 'sort_by') {
            this.props.resetCourseDishonestyPage()
            this.props.getDishonestyReport(`${url}/api/classCheating?projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy(value)}&page=1`)
        }

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
                    <SyncItem />
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
        page: state.course && state.course.dishonestyPage ? state.course.dishonestyPage : 1,
        last: state.course && state.course.getDishonestyReportData ? state.course.getDishonestyReportData.last : true,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
        getDishonestyReport: (url, headers, body) => dispatch(getDishonestyReport(url, headers, body)),
        setModalState: (id) => dispatch(setModalState(id)),
        updateCourseDishonestyPage: () => dispatch(updateCourseDishonestyPage()),
        resetCourseDishonestyPage: () => dispatch(resetCourseDishonestyPage()),
    }
}

export default connect(mapStateToProps, mapDispatchToProps, null, { withRef: true })(CourseDishonestyPanel)