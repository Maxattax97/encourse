import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import CourseStudentSummary from './StudentSummary'
import {toggleSelectAllCards, resetFilterState, setFilterState} from "../../../redux/actions"
import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {getStudents} from "../../../redux/state-peekers/course"
import {getCurrentProject} from '../../../redux/state-peekers/projects'

class StudentFilter extends Component {

    sort_by_values = ['Name', 'Hours', 'Commits', 'Progress']
    order_values = ['Ascending', 'Descending']
	view_values = ['All Tests', 'Visible Tests', 'Hidden Tests']

    componentDidMount() {
        this.props.setFilterState("sort_by", 0);
        this.props.setFilterState("order_by", 0);
    }

    componentWillUnmount() {
        this.props.resetFilterState()
    }

	render() {
        return (
            <div className='course-students'>
                <h3 className='header'>Students { this.props.students.data.length ? '(' + this.props.students.data.length + ')' : '' }</h3>
                 {
                    !this.props.students.loading && !this.props.students.error ?
                        <Filter>
                            {/*<Checkbox onClick={() => this.props.toggleSelectAllCards()}>
                                {
                                    this.props.selectedAllStudents ?
                                        <CheckmarkIcon/>
                                        : null
                                }
	                        </Checkbox>*/}

                            <Dropdown header='h5'
	                                  text='Sort by'
	                                  values={this.sort_by_values}
                                      filter='sort_by'
	                                  left/>

                            <Dropdown header='h5'
                                      text='Order'
                                      values={this.order_values}
                                      filter='order_by'
                                      right />

                            {/*<Dropdown header='h5'
                                      text='Commits'
                                      values={this.getFilterCommitValues()}
                                      filter='commit_filter'
                                      addCustom
                                      right />

                            <Dropdown header='h5'
                                      text='Hours'
                                      values={this.getFilterTimeValues()}
                                      filter='hour_filter'
                                      addCustom
                                      right/>

                            <Dropdown header='h5'
                                      text='Progress'
                                      values={this.getFilterProgressValues()}
                                      filter='progress_filter'
                                      addCustom
                                      right />*}

                            {
                                this.props.project && this.props.project.runTestall ?
                                    <Dropdown header='h5'
                                              text='View'
                                              values={this.view_values}
                                              filter='view_filter'
                                              left/>
                                    : null
                            }*/}

                        </Filter>
                        :
                        null
                }
                <CourseStudentSummary />
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: getStudents(state),
        selectedAllStudents: getAllSelected(state, 'students'),
        filters: getFilters(state),
        project: getCurrentProject(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
	    toggleSelectAllCards: () => dispatch(toggleSelectAllCards('students')),
        resetFilterState: () => dispatch(resetFilterState()),
        setFilterState: (id, value) => dispatch(setFilterState(id, value)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentFilter)