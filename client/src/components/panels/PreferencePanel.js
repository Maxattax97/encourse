import React, { Component } from 'react'
import Card from "../Card";
import settingsIcon from "../../img/settings.svg";
import StudentPreview from "./util/StudentPreview";


class PreferencePanel extends Component {

    constructor(props) {
        super(props);

        this.state = {
            courses: [{
                name: "CS252",
                semester: "Fall 2018"
            }]
        }
    }

    render() {
        return (
            <div className="panel-preference">
                <div className="panel-center-content">
                    <div className="panel-preference-content">
                        <div className="title">
                            <h1>Admin Preferences</h1>
                        </div>
                        <h1 className="break-line title" />
                        <h3>Courses</h3>
                        <div className="panel-course-students float-height">
                            {
                                this.state.courses &&
                                this.props.courses.map((course) =>
                                    <Card key={`${course.name}-${course.semester}`}
                                          component={<div></div>}
                                          />)
                            }
                        </div>
                        <Card component={
                            <div className="course-container">

                            </div>
                        } />
                    </div>
                </div>
            </div>
        )
    }
}

export default PreferencePanel