import React, { Component } from 'react'
import { connect } from 'react-redux'

import { history } from '../../redux/store'
import url from '../../server'
import { getStudentPreviews, setCurrentProject, setCurrentStudent } from '../../redux/actions/index'
import ProjectNavigation from '../navigation/ProjectNavigation'
import StudentPreview from './util/StudentPreview'
import ClassProgressHistogram from '../chart/ClassProgressHistogram'
import ClassTestCasePercentDone from '../chart/ClassTestCasePercentDone'
import ActionNavigation from '../navigation/ActionNavigation'
import CourseModal from '../modal/CourseModal'
import {Title, Summary, Card, SettingsIcon, Filter, Dropdown} from '../Helpers'

import { fuzzing } from '../../fuzz'

class CoursePanel extends Component {

    constructor(props) {
        super(props)

        this.state = {
            modal_blur: '',
            sort_students_by: 0,
            filter_students_by: 0,
            display_students_menu: 0,
            commit_ranges: ['1 - 10', '11 - 25', '26 - 100', '101 - 500', '500+'],
            time_ranges: ['1 - 5', '6 - 10', '11 - 25', '26+']
        }
    }

    componentDidMount = () => {
        this.props.getStudentPreviews(`${url}/api/studentsData?courseID=cs252&semester=Fall2018`)
    }

    showStudentPanel = (student) => {
        this.props.setCurrentStudent(student)
        if (fuzzing) {
            // NOTE: we don't even use the student id in the url
            history.push('/student/student')
        } else {
            history.push(`/student/${student.id}`)
        }
    };

    render() {
        return (
            <div className='panel-course'>
                <ProjectNavigation onModalBlur={ (blur) => this.setState({modal_blur : blur ? ' blur' : ''}) }
                    {...this.props}/>

                <div className='panel-right-nav'>
                    <div className='top-nav'>
                        <div className='course-repository-info'>
                            <h4>Last Sync:</h4>
                            <h4>Last Test Ran:</h4>
                        </div>
                    </div>
                    <ActionNavigation actions={[
                        () => { history.push('/manage-tas') },
                        () => {  },
                        () => {  },
                        () => { history.push('/course-dishonesty') }
                    ]}
                    action_names={[
                        'Manage Teaching Assistants',
                        'Sync Repositories',
                        'Run Tests',
                        'Academic Dishonesty Report'
                    ]}/>

                </div>

                <CourseModal show={ this.state.show_course_options }
                    close={ () => this.setState({ show_course_options: false, modal_blur: '' }) }/>

                <div className='panel-center-content'>

                    <div className={ `panel-course-content${this.state.modal_blur}` }>
                        <Title onClick={ () => this.setState({ show_course_options: true, modal_blur: ' blur' }) }
                            header={ <h1 className='header'>CS252</h1> }
                            icon={ <SettingsIcon/> } />
                        <div className='h1 break-line header' />

                        <Summary
                            columns={ 2 }
                            data={ [
                                <ClassProgressHistogram projectID={this.props.currentProjectId} key={1}/>,
                                <ClassTestCasePercentDone projectID={this.props.currentProjectId} key={2}/>
                            ] }
                            className='charts'
                            iterator={ (chart) => <Card key={ chart.key }>
                                {
                                    chart
                                }
                            </Card> } >
                            <h3 className='header'>Course Charts Summary</h3>
                        </Summary>

                        <div className='h1 break-line' />

                        <h3 className='header'>Students Summary</h3>
                        {
                            this.props.students && this.props.students.length > 0 ?
                                <Filter>
                                    <Dropdown header={<h5>Sort by Name</h5>}
                                        onClick={ (index) => {}}
                                        leftAnchor>

                                        <h5>Sort by Name</h5>
                                        <h5>Sort by Hours</h5>
                                        <h5>Sort by Commits</h5>
                                        <h5>Sort by Progress</h5>
                                    </Dropdown>
                                    <Dropdown header={<h5>Ascending Order</h5>}
                                        onClick={ (index) => {}}
                                        rightAnchor>

                                        <h5>Ascending Order</h5>
                                        <h5>Descending Order</h5>
                                    </Dropdown>
                                    <Dropdown header={<h5>Any Commits</h5>}
                                        onClick={ (index) => {}}
                                        rightAnchor>
                                        <h5>Any Commits</h5>
                                        {
                                            this.state.commit_ranges.map(range =>
                                                <h5 key={range}>
                                                    {range} Commits
                                                </h5>
                                            )
                                        }
                                    </Dropdown>
                                    <Dropdown header={<h5>Any Hours</h5>}
                                        onClick={ (index) => {}}
                                        rightAnchor>
                                        <h5>Any Hours</h5>
                                        {
                                            this.state.time_ranges.map(range =>
                                                <h5 key={range}>
                                                    {range} Hours
                                                </h5>
                                            )
                                        }
                                    </Dropdown>
                                    <Dropdown header={<h5>Any Progress</h5>}
                                        onClick={ (index) => {}}
                                        rightAnchor>
                                        <h5>Any Progress</h5>
                                        <h5>0 - 25% Progress</h5>
                                        <h5>26 - 50% Progress</h5>
                                        <h5>51 - 100% Progress</h5>
                                    </Dropdown>
                                </Filter>
                                :
                                null
                        }
                        <Summary
                            columns={ 5 }
                            data={ this.props.students }
                            className='course-students'
                            iterator={ (student) =>
                                <StudentPreview key={ student.id }
                                    student={ student }
                                    projectID={ this.props.currentProjectId }
                                    onClick={ () => this.showStudentPanel(student) }/>
                            } />
                    </div>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : [],
        currentProjectId: state.projects && state.projects.currentProjectId ? state.projects.currentProjectId : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getStudentPreviews: (url, headers, body) => dispatch(getStudentPreviews(url, headers, body)),
        setCurrentProject: (id, index) => dispatch(setCurrentProject(id, index)),
        setCurrentStudent: (student) => dispatch(setCurrentStudent(student)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoursePanel)
