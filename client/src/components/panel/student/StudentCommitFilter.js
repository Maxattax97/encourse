import React, { Component } from 'react'
import {Checkbox, CheckmarkIcon, Dropdown, Filter} from '../../Helpers'
import { connect } from 'react-redux'

import {getAllSelected, getFilters} from "../../../redux/state-peekers/control"
import {resetFilterState, setFilterState, toggleSelectAllCards} from "../../../redux/actions"
import StudentCommitSummary from './StudentCommitSummary'

class StudentCommitFilter extends Component {

    sort_by_values = ['Name', 'Additions', 'Deletions']
    bundle_by_values = ['Nothing', 'Day']
    order_values = ['Ascending', 'Descending']
    addition_values = ['Any', '1']
    deletion_values = ['Any', '2']

    componentWillUnmount() {
        this.props.resetFilterState()
    }

    render() {
        return (
            <div className='project-tests'>
                <h3 className='header'>Commits</h3>
                <Filter>
                    <Checkbox onClick={() => this.props.toggleSelectAllCards()}>
                        {
                            this.props.selectedAllTests ?
                                <CheckmarkIcon/>
                                : null
                        }
                    </Checkbox>

                    <Dropdown header='h5'
                              text='Sort by'
                              values={this.sort_by_values}
                              filter='sort_by'
                              left/>

                    <Dropdown header='h5'
                              text='Bundle by'
                              values={this.bundle_by_values}
                              filter='bundle_by'
                              left
                    />

                    <Dropdown header='h5'
                              text='Order'
                              values={this.order_values}
                              filter='order_by'
                              right />

                    <Dropdown header='h5'
                              text='Additions'
                              values={this.addition_values}
                              filter='addition_filter'
                              addCustom
                              right/>

                    <Dropdown header='h5'
                              text='Deletions'
                              values={this.deletion_values}
                              filter='deletion_filter'
                              addCustom />
                </Filter>
                <StudentCommitSummary/>
            </div>
        )
    }
}


const mapStateToProps = (state) => {
    return {
        selectedAllTests: getAllSelected(state, 'commits'),
        filters: getFilters(state)
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        toggleSelectAllCards: () => dispatch(toggleSelectAllCards('commits')),
        resetFilterState: () => dispatch(resetFilterState())
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(StudentCommitFilter)