import React, { Component } from 'react'
import {Dropdown, Filter} from '../../Helpers'
import connect from 'react-redux/es/connect/connect'
import CourseStudentSummary from './CourseStudentSummary'

class CourseStudentFilter extends Component {

    sort_by_ranges = ['Name', 'Hours', 'Commits', 'Progress']
    order_ranges = ['Ascending', 'Descending']
    commit_ranges = ['Any', '1 - 10', '11 - 25', '26 - 100', '101 - 500', '500+']
    time_ranges = ['Any', '1 - 5', '6 - 10', '11 - 25', '26+']
    progress_ranges = ['Any', '0 - 25%', '26 - 50%', '51 - 75%', '76 - 100%']

    constructor(props) {
        super(props)

        this.state = {
            sort_by: 0,
            order_by: 0,
            commit_filter: 0,
            hour_filter: 0,
            progress_filter: 0
        }
    }

    render() {
        return (
            <div className='course-students'>
                {
                    this.props.students && this.props.students.length > 0 ?
                        <Filter>
                            <Dropdown header={<h5>Sort by { this.sort_by_ranges[this.state.sort_by] }</h5>}
                                onClick={ (index) => { this.setState({ sort_by: index }) }}
                                leftAnchor>

                                {
                                    this.sort_by_ranges.map(range =>
                                        <h5 key={range}>
                                            Sort by {range}
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.order_ranges[this.state.order_by] } Order</h5>}
                                onClick={ (index) => { this.setState({ order_by: index }) }}
                                rightAnchor>
                                {
                                    this.order_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Order
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.commit_ranges[this.state.commit_filter] } Commits</h5>}
                                onClick={ (index) => { this.setState({ commit_filter: index }) }}
                                rightAnchor>
                                {
                                    this.commit_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Commits
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.time_ranges[this.state.hour_filter] } Hours</h5>}
                                onClick={ (index) => { this.setState({ hour_filter: index }) }}
                                rightAnchor>
                                {
                                    this.time_ranges.map(range =>
                                        <h5 key={range}>
                                            {range} Hours
                                        </h5>
                                    )
                                }
                            </Dropdown>
                            <Dropdown header={<h5>{ this.progress_ranges[this.state.progress_filter] } Progress</h5>}
                                onClick={ (index) => { this.setState({ progress_filter: index }) }}
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
        students: state.course && state.course.getStudentPreviewsData ? state.course.getStudentPreviewsData : []
    }
}

export default connect(mapStateToProps, null)(CourseStudentFilter)