import React, { Component } from 'react'

import {CheckmarkIcon, Modal} from '../../Helpers'

class ProjectZipModal extends Component {

    constructor(props) {
        super(props)

        this.state = {
            zip_filename: '',
            zip_file: null,
            json_filename: '',
            json_file: null
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
                    <h2 className='header'>Upload Test Zip</h2>
                    <div className="h2 break-line header"/>
                    <h4 className='header'>
                        Zip
                    </h4>
                    <input type="text" className="h3-size" value={this.state.zip_filename} onClick={ () => this.refs.zipUploader.click()}/>
                    <input type="file" style={{display:'none'}} ref="zipUploader" onChange={ (e) => console.log(e) }/>
                    <div className="h5 break-line"/>
                    <h4 className='header'>
                        Json
                    </h4>
                    <input type="text" className="h3-size" value={this.state.json_filename} onClick={ () => this.refs.jsonUploader.click()}/>
                    <input type="file" style={{display:'none'}} ref="jsonUploader" onChange={ (e) => console.log(e) }/>
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

export default ProjectZipModal