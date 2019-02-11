import {Component} from 'react'
import React from 'react'
import {connect} from 'react-redux'
import {history} from '../../redux/store'
import {getCourse, getCurrentCourseId, getCurrentSemesterId} from '../../redux/state-peekers/course'

class BackNavigation extends Component {
    render() {
        return (
            <div className='top-nav back-nav svg-icon float-height action' onClick={ () => history.push(`/${this.props.currentCourseId}/course`)}>
                <h1>
                    {
                        !this.props.course.loading && !this.props.course.error ?
                            this.props.course.data.name :
                            null
                    }
                </h1>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentCourseId: getCurrentCourseId(state),
        course: getCourse(state)
    }
}

export default connect(mapStateToProps)(BackNavigation)