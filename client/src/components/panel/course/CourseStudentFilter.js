import React, { Component } from 'react'
import {Checkbox, Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import CourseStudentSummary from './CourseStudentSummary'

class CourseStudentFilter extends Component {

    sort_by_ranges = ['Name', 'Hours', 'Commits', 'Progress']
    order_ranges = ['Ascending', 'Descending']
    commit_ranges = ['Any', '1 - 10', '11 - 25', '26 - 100', '101 - 500', '500+']
    time_ranges = ['Any', '1 - 5', '6 - 10', '11 - 25', '26+']
    progress_ranges = ['Any', '0 - 25%', '26 - 50%', '51 - 75%', '76 - 100%']

    render() {
        return (
            <div className='course-students'>
                <h3 className='header'>Students Summary</h3>
                {
                    this.props.students && this.props.students.length > 0 ?
                        <Filter>
                            <Dropdown header={<h5>Sort by { this.sort_by_ranges[this.props.filters.sort_by] }</h5>}
                                onClick={ (index) => this.props.onChange('sort_by', index) }
                                leftAnchor>

                                {
                                    this.sort_by_ranges.map(range =>
                                        <h5 key={range}>
                                            Sort by {range}
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.order_ranges[this.props.filters.order_by] } Order</h5>}
                                onClick={ (index) => this.props.onChange('order_by', index) }
                                rightAnchor>
                                {
                                    this.order_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Order
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.commit_ranges[this.props.filters.commit_filter] } Commits</h5>}
                                onClick={ (index) => this.props.onChange('commit_filter', index) }
                                rightAnchor>
                                {
                                    this.commit_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Commits
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.time_ranges[this.props.filters.hour_filter] } Hours</h5>}
                                onClick={ (index) => this.props.onChange('hour_filter', index) }
                                rightAnchor>
                                {
                                    this.time_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Hours
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.progress_ranges[this.props.filters.progress_filter] } Progress</h5>}
                                onClick={ (index) => this.props.onChange('progress_filter', index) }
                                rightAnchor>
                                {
                                    this.progress_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Progress
                                        </h5>
                                    )
                                }
                            </Dropdown>
                        </Filter>
                        :
                        null
                }
                <CourseStudentSummary/>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData.content : []
    }
}

export default connect(mapStateToProps, null)(CourseStudentFilter)