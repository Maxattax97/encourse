import React from 'react'
import Enzyme, { shallow, mount } from 'enzyme'
import Adapter from 'enzyme-adapter-react-16'
import { ClassProgressHistogram } from './ClassProgressHistogram'

Enzyme.configure({ adapter: new Adapter() })

function setup() {
  const props = {
    getData: jest.fn(),
    token: null
  }

  const wrapper = mount(<ClassProgressHistogram {...props} />)

  return {
    props,
    wrapper
  }
}

describe('<ClassProgressHistogram />', () => {
  it('render()', () => {
    const { wrapper } = setup()
  })

  it('handle property data changes', async () => {
    const { wrapper } = setup()
    const componentWillReceivePropsSpy = jest.spyOn(wrapper.instance(), 'componentWillReceiveProps')
    const formatApiDataSpy = jest.spyOn(wrapper.instance(), 'formatApiData')

    wrapper.setProps({
        data: [
            { "0-50%": 5 },
            { "50-100%": 10 },
        ],
        isFinished: true,
    })
    await new Promise(resolve => process.nextTick(resolve))
    wrapper.update()
    expect(componentWillReceivePropsSpy).toHaveBeenCalledTimes(1)
    expect(formatApiDataSpy).toHaveBeenCalledTimes(1)

    wrapper.setProps({ data: [
      { "0-50%": 10 },
      { "50-100%": 5 },
    ] })
    await new Promise(resolve => process.nextTick(resolve))
    wrapper.update()
    expect(componentWillReceivePropsSpy).toHaveBeenCalledTimes(2)
    expect(formatApiDataSpy).toHaveBeenCalledTimes(2)
  })
})
