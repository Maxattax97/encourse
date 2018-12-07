import React, { Component } from 'react'
import ActionNavigation from '../navigation/ActionNavigation'
import {Title} from '../Helpers'
import {getStudentPreviews, setCurrentStudent, setModalState, getDishonestyReport, updateCourseDishonestyPage, resetCourseDishonestyPage} from '../../redux/actions'
import {getCurrentCourseId, getStudents} from '../../redux/state-peekers/course'
import url from '../../server'
import { connect } from 'react-redux'
import StudentReportFilter from './course-dishonesty/StudentReportFilter'
import CourseDishonestyCharts from './course-dishonesty/CourseDishonestyCharts'
import ShareReportModal from './common/ShareReportModal'
import TaskModal from './common/TaskModal'
import ProjectNavigation from '../navigation/ProjectNavigation'
import {getCurrentProject} from '../../redux/state-peekers/projects'
import BackNavigation from '../navigation/BackNavigation'
import {retrieveTestScripts} from '../../redux/retrievals/projects'

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

    componentDidMount = () => {
        if(this.props.project)
            this.props.getDishonestyReport(`${url}/api/classCheating?projectID=${this.props.project.id}`)
    }

    componentDidUpdate(prevProps) {
        if(this.props.project && (!(prevProps.project) || prevProps.project.index !== this.props.project.index))
            this.props.getDishonestyReport(`${url}/api/classCheating?projectID=${this.props.project.id}`)
    }

    scrolledToBottom = () => {
        if(this.props.project && !this.props.last) {
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
            default:
                return 'id'
        }
    }

    changeFilter = (key, value) => {
        const filters = [...this.state.filters]
        filters[key] = value

        if(key === 'sort_by') {
            this.props.resetCourseDishonestyPage()
            this.props.getDishonestyReport(`${url}/api/classCheating?projectID=${this.props.currentProjectId}&sortBy=${this.getSortBy(value)}&page=1`)
        }

        this.setState({ filters })
    }

    share = () => {
        this.props.setModalState(2)
    }

    render() {

        const action_names = [
            'Current Task',
            'Share Results'
        ]

        const actions = [
            () => { this.props.setModalState(1) },
            this.share
        ]

        return (
            <div className="class-dishonesty-panel">

                <div className='panel-left-nav'>
                    <BackNavigation/>
                    <ProjectNavigation/>
                    <ActionNavigation actions={ actions } action_names={ action_names }/>
                </div>

                <TaskModal id={1} />
                <ShareReportModal id={2} link={window.location}/>

                <div className='panel-center-content'>

                    <div className='panel-course-report'>
                        <Title>
                            <h1 className='header'>Academic Dishonesty Report</h1>
                        </Title>
                        <div className='h1 break-line header' />

                        <h3 className='header'>Course Charts Summary</h3>
                        <CourseDishonestyCharts/>

                        <div className='h1 break-line' />

                        <StudentReportFilter report={this.props.report}/>
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        currentCourseId: getCurrentCourseId(state),
        report: state.course && state.course.getDishonestyReportData ? state.course.getDishonestyReportData.content : [],
        project: getCurrentProject(state),
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