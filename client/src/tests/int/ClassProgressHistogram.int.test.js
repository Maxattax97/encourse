import React from 'react'
import Enzyme, { shallow, mount } from 'enzyme'
import Adapter from 'enzyme-adapter-react-16'
import { Provider } from 'react-redux';
import ClassProgressHistogram from '../../components/charts/ClassProgressHistogram'
import myReducer from '../../redux/reducers';
import { setupIntegrationTest } from '../util/reduxTestUtils'

Enzyme.configure({ adapter: new Adapter() })

describe('<ClassProgressHistogram />', () => {
  let store;
  let dispatchSpy;

  beforeEach(() => {
    ({ store, dispatchSpy } = setupIntegrationTest({ myReducer }));
  });

  function setup() {
    const props = {
      getData: jest.fn(),
      token: null,
    }

    const enzymeWrapper = mount(
      <Provider store={store}>
        <ClassProgressHistogram {...props} />
      </Provider>
    )

    return {
      props,
      enzymeWrapper
    }
  }

  it('render()', () => {
    const { enzymeWrapper } = setup()
  })
})
