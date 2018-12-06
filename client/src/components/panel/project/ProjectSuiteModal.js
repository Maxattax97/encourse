import React, { Component } from 'react'
import { connect } from 'react-redux'
import 'rc-calendar/assets/index.css';

import {CheckmarkIcon, Modal} from '../../Helpers'
import moment from "moment"

class ProjectSuiteModal extends Component {

    constructor(props) {
        super(props)

        this.state = {
            name: '',
            source_name: '',
            due_date: moment(),
            date_picker: false
        }
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: event.target.value })
    };

    saveSettings = () => {

    };

    render() {
        return (
            <div className="course-modal">
                <Modal center id={this.props.id}>
                    <h2 className='header'>{ this.props.newTestSuite ? 'New Test Suite' : 'Test Suite Settings' }</h2>
                    <div className="h2 break-line header"/>
                    <h4 className="header">
                        Name
                    </h4>
                    <input type="text" className="h3-size" value={this.props.courseID} name="name" autoComplete="off"/>
                    <div className="modal-buttons float-height">
                        <div className="svg-icon action" onClick={ this.saveSettings }>
                            <CheckmarkIcon/>
                        </div>
                    </div>
                </Modal>
            </div>
        )
    }

}

const mapStateToProps = (state) => {
    return {
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectSuiteModal)