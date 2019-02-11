
export function hourMinutesFromMinutes(minutes) {
    if(minutes % 60 === 0)
        return `${minutes / 60}h`

    if(minutes < 60)
        return `${minutes}m`

    return `${Math.floor(minutes / 60)}h ${minutes % 60}m`;
}