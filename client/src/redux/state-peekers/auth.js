const createPeeker = (name) => (state) => state.auth && state.auth[name] ? state.auth[name] : { loading: true, data: [] }

export const getAccount = createPeeker('getAccount')