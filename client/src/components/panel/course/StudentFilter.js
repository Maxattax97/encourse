import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import CourseStudentSummary from './StudentSummary'
import {toggleSelectAllCards, resetFilterState, setFilterState} from "../../../redux/actions"

class StudentFilter extends Component {

    sort_by_values = ['Name', 'Hours', 'Commits', 'Progress']
    order_values = ['Ascending', 'Descending']
    commit_values = ['Any', '1 - 10', '11 - 25', '26 - 100', '101 - 500', '500+']
    time_values = ['Any', '1 - 5', '6 - 10', '11 - 25', '26+']
    progress_values = ['Any', '0 - 25%', '26 - 50%', '51 - 75%', '76 - 100%']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

	render() {
        return (
            <div className='course-students'>
                <h3 className='header'>Students Summary</h3>
                {
                    this.props.students ?
                        <Filter>
	                        <Checkbox onClick={() => this.props.toggleSelectAllCards()}>
                                {
                                    this.props.selectedAllStudents === 2 ?
                                        <CheckmarkIcon/>
                                        : null
                                }
	                        </Checkbox>

	                        <Dropdown header='h5'
	                                  text='Sort by'
	                                  values={this.sort_by_values}
	                                  currentIndex={this.props.filters.sort_by}
	                                  onClick={ (index) => this.props.setFilterState('sort_by', index) }
	                                  left/>

                            <Dropdown header='h5'
                                      text='Order'
                                      values={this.order_values}
                                      currentIndex={this.props.filters.order_by}
                                      onClick={ (index) => this.props.setFilterState('order_by', index) }
                                      right />

                            <Dropdown header='h5'
                                      text='Commits'
                                      values={this.commit_values}
                                      currentIndex={this.props.filters.commit_filter}
                                      onClick={ (index) => this.props.setFilterState('commit_filter', index) }
                                      right />

                            <Dropdown header='h5'
                                      text='Hours'
                                      values={this.time_values}
                                      currentIndex={this.props.filters.hour_filter}
                                      onClick={ (index) => this.props.setFilterState('hour_filter', index) }
                                      right/>

                            <Dropdown header='h5'
                                      text='Progress'
                                      values={this.progress_values}
                                      currentIndex={this.props.filters.progress_filter}
                                      onClick={ (index) => this.props.setFilterState('progress_filter', index) }
                                      right />
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
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : [],
        selectedAllStudents: state.control && state.control.selectedAllStudents ? state.control.selectedAllStudents : 0,
        filters: state.control && state.control.filters ? state.control.filters : {}
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
	    toggleSelectAllCards: () => dispatch(toggleSelectAllCards('students')),
        setFilterState: (id, value) => dispatch(setFilterState(id, value)),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentFilter)