
export function isAccountNotTA(account) {
    return account.loading === false && account.data[0].role !== 1;
}

export function isAccountTA(account) {
    return account.loading === false && account.data[0].role === 1;
}