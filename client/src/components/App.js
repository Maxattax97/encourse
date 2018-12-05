import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'
import { history } from '../redux/store'
import { defaultCourse, defaultSemester } from '../defaults'
import { getCurrentCourseId, getCurrentSemesterId } from '../redux/state-peekers/course'
import { setCurrentCourse, setCurrentSemester } from '../redux/actions'
import '../styles/css/login.css'
import Login from './Login'
import Main from './Main'

class App extends Component {

    constructor(props) {
        super(props)

        this.state = {
            prevRoute: ''
        }
    }

    componentDidMount = () => {
        if(this.loggedIn()) {
            const course = this.props.match.params.courseID ? this.props.match.params.courseID : defaultCourse
            const semester = this.props.match.params.semesterID ? this.props.match.params.semesterID : defaultSemester
            const page = this.props.path.substring(this.props.path.lastIndexOf('/') + 1)
            console.log(page)
            if(/^((Fall)|(Spring)|(Summer))2[0-9][0-9][0-9]$/.test(semester)) {
                this.props.setCurrentSemester(semester)
            } 
    
            if(/^[a-z]+[0-9]{3,}$/.test(course)) {
                this.props.setCurrentCourse(course)
            } 

            history.push(`/${course}/${semester}/${page}`)
        }
    }

    loggedIn = () => {
        return this.props.token != null
    }

    updateState = () => {
        return this.setState({ prevRoute: history.location.pathname})
    }

    render() {
        return (
            <div className="App">
                <Switch>
                    <Route path="/login" render={(navProps) => {
                        console.log(this.props.prevRoute)
                        return !this.loggedIn()
                            ? <Login {...navProps} />
                            : <Redirect to={`/${this.props.currentCourseId}/${this.props.currentSemesterId}/course`}/>
                    }

                    }/>
                    <Route path="/" onChange={this.updateState} render={(navProps) => {
                        return this.loggedIn()
                            ? <Main {...navProps} />
                            : <Redirect to={{
                                pathname: '/login',
                                state: { prevRoute: history.location.pathname }
                            }} />
                    }
                       
                    }/>
                </Switch>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth && state.auth.logInData ? state.auth.logInData.access_token : null,
        path: state.router && state.router.location ? state.router.location.pathname : null,   
        currentCourseId: getCurrentCourseId(state),
        currentSemesterId: getCurrentSemesterId(state),
        prevRoute: state.auth ? state.auth.location : null
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        setCurrentCourse: (id) => dispatch(setCurrentCourse(id)),
        setCurrentSemester: (id) => dispatch(setCurrentSemester(id)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
