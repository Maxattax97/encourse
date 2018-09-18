import React, { Component } from 'react'

import '../../css/CoursePanel.css'
import settingsIcon from '../../img/settings.svg'

class CoursePanel extends Component {

    render() {
        return (
                <div className="Course-Panel">
                    <div className="Course-Settings">
                        <div className="Settings-Title" onClick={() => this.props.history.push("/project-settings")}>
                            <div className="Project-Title">
                                Projects
                            </div>
                            <div className="Project-Settings">
                                <img src={settingsIcon} />
                            </div>
                        </div>
                        <div className="Break-Line" />
                        <div className="Project">
                            MyMalloc1
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                        <div className="Project">
                            MyMalloc2
                        </div>
                    </div>
                    <div className="Course-Content">
                    </div>
                </div>
        )
    }
}

export default CoursePanel