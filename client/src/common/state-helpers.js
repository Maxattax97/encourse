
export function isAccountNotTA(account) {
    return !account.loading && !account.error && account.data.role !== 0;
}

export function isAccountTA(account) {
    console.log(account, !account.loading, !account.error, account.data.role === 0)
    return !account.loading && !account.error && account.data.role === 0;
}