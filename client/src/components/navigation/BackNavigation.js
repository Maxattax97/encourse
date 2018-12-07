import {Component} from 'react'
import React from 'react'
import {connect} from 'react-redux'
import {history} from '../../redux/store'
import {getCurrentCourseId, getCurrentSemesterId} from '../../redux/state-peekers/course'

class BackNavigation extends Component {
    render() {
        return (
            <div className='top-nav back-nav svg-icon float-height action' onClick={ () => history.push(`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`)}>
                <h1>
                    { this.props.currentCourseId }
                </h1>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
    }
}

export default connect(mapStateToProps)(BackNavigation)