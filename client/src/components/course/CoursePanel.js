import React, { Component } from 'react'

import '../../css/CoursePanel.css'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'

class CoursePanel extends Component {
    render() {
        return (
                <div className="Course-Panel">
                    <div className="Course-Settings">
                        <div className="Break-Line" />
                        <table className="Projects">
                            <tr>
                                <th>Project Name</th>
                                <th>Source Name</th>
                                <th>Created</th>
                                <th>Due</th>
                            </tr>
                            <tr><th colspan="4" className="Break-Line" /></tr>
                            <tr className="Project">
                                <td>MyMalloc</td>
                                <td>lab1-src</td>
                                <td>9/05/18</td>
                                <td>9/15/18</td>
                            </tr>
                            <tr className="Project">
                                <td>MyMalloc</td>
                                <td>lab1-src</td>
                                <td>9/05/18</td>
                                <td>9/15/18</td>
                            </tr>
                            <tr className="Project">
                                <td>MyMalloc</td>
                                <td>lab1-src</td>
                                <td>9/05/18</td>
                                <td>9/15/18</td>
                            </tr>
                            <tr className="Project">
                                <td>MyMalloc</td>
                                <td>lab1-src</td>
                                <td>9/05/18</td>
                                <td>9/15/18</td>
                            </tr>
                        </table>
                    </div>
                </div>
        )
    }
}

export default CoursePanel