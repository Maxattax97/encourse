
export function isAccountNotTA(account) {
    return !account.loading && !account.error && account.data.role !== 0;
}

export function isAccountTA(account) {
    return !account.loading && !account.error && account.data.role === 0;
}