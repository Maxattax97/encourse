
let fakeUid = 1
export const getFakeUid = () => {
    return fakeUid++
}

export const realToFakeMapping = {};
export const fakeToRealMapping = {};
export const fuzzing = false;
