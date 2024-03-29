import React from 'react'
import Enzyme, { mount } from 'enzyme'
import Adapter from 'enzyme-adapter-react-16'
import { Provider } from 'react-redux'
import ClassProgressHistogram from '../../components/panel/course/chart/StudentsCompletionProgress'
import myReducer from '../../redux/reducers'
import { setupIntegrationTest } from '../util/reduxTestUtils'

Enzyme.configure({ adapter: new Adapter() })

describe('<StudentsCompletionProgress />', () => {
    let store
    // let dispatchSpy

    beforeEach(() => {
        ({ store, /* dispatchSpy */ } = setupIntegrationTest({ myReducer }))
    })

    function setup() {
        const props = {
            getData: jest.fn(),
            token: null,
        }

        const wrapper = mount(
            <Provider store={store}>
                <ClassProgressHistogram {...props} />
            </Provider>
        )

        return {
            props,
            wrapper
        }
    }

    it('render()', () => {
        setup()
    })
})
