import React, { Component } from 'react'

import {CheckmarkIcon, Modal} from '../../Helpers'
import {getFilterModalType} from '../../../redux/state-peekers/control'
import {setFilterState, setModalState} from '../../../redux/actions'
import connect from 'react-redux/es/connect/connect'

class CustomRangeModal extends Component {

    constructor(props) {
        super(props)

        this.state = {
            start: null,
            end: null
        }
    }

    componentDidUpdate(prevProps) {
        if(!this.props.type)
            return

        const start = (this.props.type.value || {}).start
        const end = (this.props.type.value || {}).end

        if(!prevProps.type) {
            this.setState({
                start,
                end
            })
            return
        }

        const prevStart = (prevProps.type.value || {}).start
        const prevEnd = (prevProps.type.value || {}).end

        if(prevProps.type.id !== this.props.type.id || prevStart !== start || prevEnd !== end)
            this.setState({
                start,
                end
            })
    }

    onChange = (event) => {
        this.setState({ [event.target.name]: parseInt(event.target.value) })
    }

    saveCustomRange = () => {
        const start = this.state.start
        const end = this.state.end

        this.setState({
            start: null,
            end: null
        })

        this.props.setModalState(0)

        if(this.props.type && (this.state.start || this.state.end))
            this.props.setFilterState(this.props.type.id, {start: start || 0, end: end || start})
    }

    render() {
        return (
            <div className="course-modal">
                <Modal center id={-1}>
                    <h2 className='header'>Custom Range</h2>
                    <div className="h4 break-line header"/>
                    <h4 className='header'>
                        Start
                    </h4>
                    <input type="number" className="h3-size" value={String(this.state.start)} onChange={this.onChange} name="start" autoComplete="off" placeholder='Start number'/>
                    <h4 className='header'>
                        End
                    </h4>
                    <input type="number" className="h3-size" value={String(this.state.end)} onChange={this.onChange} name="end" autoComplete="off" placeholder='End number'/>
                    <div className="modal-buttons float-height">
                        <div className="svg-icon action" onClick={ this.saveCustomRange }>
                            <CheckmarkIcon/>
                        </div>
                    </div>
                </Modal>
            </div>
        )
    }
}

function mapStateToProps(state) {
    return {
        type: getFilterModalType(state)
    }
}

function mapDispatchToProps(dispatch) {
    return {
        setFilterState: (id, value) => dispatch(setFilterState(id, value)),
        setModalState: (id) => dispatch(setModalState(id))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CustomRangeModal)
