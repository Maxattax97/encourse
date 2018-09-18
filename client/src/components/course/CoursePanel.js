import React, { Component } from 'react'
import { LineChart } from 'rd3'

import '../../css/CoursePanel.css'
import Card from '../Card'
import CourseSettings from './CourseSettings'
import StudentPreview from './StudentPreview'
import settingsIcon from '../../img/settings.svg'

class CoursePanel extends Component {

    render() {
        let lineData = [
            { 
              name: 'series1',
              values: [ { x: 0, y: 20 }, { x: 1, y: 30 }, { x: 2, y: 10 }, { x: 3, y: 5 }, { x: 4, y: 8 }, { x: 5, y: 15 }, { x: 6, y: 10 } ],
              strokeWidth: 3,
              strokeDashArray: "5,5",
            },
            {
              name: 'series2',
              values : [ { x: 0, y: 8 }, { x: 1, y: 5 }, { x: 2, y: 20 }, { x: 3, y: 12 }, { x: 4, y: 4 }, { x: 5, y: 6 }, { x: 6, y: 2 } ]
            },
            {
              name: 'series3',
              values: [ { x: 0, y: 0 }, { x: 1, y: 5 }, { x: 2, y: 8 }, { x: 3, y: 2 }, { x: 4, y: 6 }, { x: 5, y: 4 }, { x: 6, y: 2 } ]
            } 
          ]
          
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
                <Card component={
                    <LineChart      
                        legend={true}
                        data={lineData}
                        width='100%'
                        height={400}
                        viewBoxObject={{
                        x: 0,
                        y: 0,
                        width: 500,
                        height: 400
                        }}
                        title="Line Chart"
                        yAxisLabel="Altitude"
                        xAxisLabel="Elapsed Time (sec)"
                        domain={{x: [,6], y: [-10,]}}
                        gridHorizontal={true}
                    />}
                />
            </div>
        )
    }
}

export default CoursePanel