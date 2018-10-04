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

  const enzymeWrapper = mount(<ClassProgressHistogram {...props} />)

  return {
    props,
    enzymeWrapper
  }
}

describe('<ClassProgressHistogram />', () => {
  it('render()', () => {
    const { enzymeWrapper } = setup()
  })
})
