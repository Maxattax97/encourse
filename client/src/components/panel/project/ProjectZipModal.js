import React, { Component } from 'react'
import { connect } from 'react-redux'

import {CheckmarkIcon, Modal} from '../../Helpers'
import {addTest} from '../../../redux/actions'
import url from '../../../server'

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

    addZIP = (event) => {
        this.setState({ zip: this.refs.zipUploader.files[0]})
    }

    addJSON = (event) => {
        var reader = new FileReader();
        reader.onload = this.onReaderLoad;
        reader.readAsText(this.refs.jsonUploader.files[0]);
    }

    onReaderLoad = (event) => {
        console.log(event.target.result);
        this.setState({ json: JSON.parse(event.target.result)})
    }

    saveSettings = () => {
        if(this.state.json && this.state.zip) {
            let form = new FormData()
            form.append('json', JSON.stringify(this.state.json))
            form.append('file', this.state.zip)
            console.log(form)
            this.props.addTest(`${url}/api/add/tests`, {}, form)
        }
    }

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
                    <input type="file" style={{display:'none'}} ref="zipUploader" onChange={ this.addZIP }/>
                    <div className="h5 break-line"/>
                    <h4 className='header'>
                        Json
                    </h4>
                    <input type="text" className="h3-size" value={this.state.json_filename} onClick={ () => this.refs.jsonUploader.click()}/>
                    <input type="file" style={{display:'none'}} ref="jsonUploader" onChange={ this.addJSON }/>
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
        addTest: (url, headers, body) => dispatch(addTest(url, headers, body)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ProjectZipModal)