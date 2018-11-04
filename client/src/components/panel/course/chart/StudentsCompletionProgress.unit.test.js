import React from 'react'
import Enzyme, { mount } from 'enzyme'
import Adapter from 'enzyme-adapter-react-16'
import { StudentsCompletionProgress } from './StudentsCompletionProgress'

Enzyme.configure({ adapter: new Adapter() })

function setup() {
    const props = {
        getData: jest.fn(),
        token: null
    }

    const wrapper = mount(<StudentsCompletionProgress {...props} />)

    return {
        props,
        wrapper
    }
}

describe('<StudentsCompletionProgress />', () => {
    it('render()', () => {
        setup()
    })

    it('handle property data changes', async () => {
        const { wrapper } = setup()
        const componentWillReceivePropsSpy = jest.spyOn(wrapper.instance(), 'componentWillReceiveProps')
        const formatApiDataSpy = jest.spyOn(wrapper.instance(), 'formatApiData')

        wrapper.setProps({
            data: [
                { '0-50%': 5 },
                { '50-100%': 10 },
            ],
            isLoading: false,
        })
        await new Promise(resolve => process.nextTick(resolve))
        wrapper.update()
        expect(componentWillReceivePropsSpy).toHaveBeenCalledTimes(1)
        expect(formatApiDataSpy).toHaveBeenCalledTimes(1)

        wrapper.setProps({
            isLoading: true,
        })
        await new Promise(resolve => process.nextTick(resolve))
        wrapper.update()
        expect(componentWillReceivePropsSpy).toHaveBeenCalledTimes(2)

        wrapper.setProps({
            data: [
                { '0-50%': 10 },
                { '50-100%': 5 },
            ],
            isLoading: false,
        })
        await new Promise(resolve => process.nextTick(resolve))
        wrapper.update()
        expect(componentWillReceivePropsSpy).toHaveBeenCalledTimes(3)
        expect(formatApiDataSpy).toHaveBeenCalledTimes(2)
    })
})
