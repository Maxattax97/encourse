import React, { Component } from 'react'

import {Card, LoadingIcon} from '../Helpers'
import {
    getCurrentCourseId,
    getCurrentSemesterId, getCurrentTA,
    getTeachingAssistants
} from '../../redux/state-peekers/course'
import connect from 'react-redux/es/connect/connect'
import {retrieveAllTeachingAssistants} from '../../redux/retrievals/course'
import {setCurrentTA} from '../../redux/actions/course'

class TANavigation extends Component {

    componentDidMount = () => {
        retrieveAllTeachingAssistants(this.props.courseId, this.props.semesterId)
    }

    render() {
        const textListDiv =
            this.props.teaching_assistants.loading && !this.props.teaching_assistants.data.length ?
                <div className='loading'>
                    <LoadingIcon/>
                </div>
                :
                this.props.teaching_assistants.data.map((ta, index) =>
                    <div key={ ta.id }
                         onClick={ () => this.props.setCurrentTA(ta, index) }
                         className={ `action${this.props.ta === ta ? ' list-highlight' : ''}` }>
                        <h4>
                            { ta.id }
                        </h4>
                    </div>
                )

        return (
            <div className="list-nav side-nav-left">
                <Card>
                    <div className="list-container">
                        <h3 className='header'>TA List</h3>
                        <div className="h3 break-line header"/>

                        <div className='text-list'>
                            {
                                textListDiv
                            }
                        </div>
                    </div>
                </Card>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        courseId: getCurrentCourseId(state),
        semesterId: getCurrentSemesterId(state),
        teaching_assistants: getTeachingAssistants(state),
        ta: getCurrentTA(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCurrentTA: (ta, index) => dispatch(setCurrentTA(ta, index))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(TANavigation)