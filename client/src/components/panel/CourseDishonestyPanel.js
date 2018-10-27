import React, { Component } from 'react'
import ActionNavigation from '../navigation/ActionNavigation'
import {history} from '../../redux/store'
import {Card, SettingsIcon, Summary, Title} from '../Helpers'

class CourseDishonestyPanel extends Component {

    back = () => {
        history.push('/course')
    }

    render() {

        const chartList = [

        ]

        return (<div className="class-dishonesty-panel">

            <div className='panel-right-nav'>
                <div className='top-nav'>
                    <div className='course-repository-info'>
                        <h4>Last Sync:</h4>
                        <h4>Last Test Ran:</h4>
                    </div>
                </div>
                <ActionNavigation actions={[
                    () => { history.push('/manage-tas') },
                    () => {  },
                    () => {  },
                    () => { history.push('/course-dishonesty') }
                ]}
                action_names={[
                    'Sync Repositories',
                    'Run Tests'
                ]}/>

            </div>

            <div className='panel-center-content'>

                <div className='panel-course-report'>
                    <Title
                        header={ <h1 className='header'>CS252 - Academic Dishonesty Report</h1> }
                        icon={ <SettingsIcon/> } />
                    <div className='h1 break-line header' />

                    <h3 className='header'>Metrics</h3>


                    <div className='h1 break-line' />

                    <h3 className='header'>Course Charts Summary</h3>
                    <Summary
                        columns={ 2 }
                        data={ chartList }
                        className='charts'
                        iterator={ (chart) => <Card key={ chart.key }>
                            { chart }
                        </Card> } >
                    </Summary>

                    <div className='h1 break-line' />

                    <h3 className='header'>Students Summary</h3>
                </div>
            </div>
        </div> )
    }
}

export default CourseDishonestyPanel