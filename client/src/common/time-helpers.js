
export function hourMinutesFromMinutes(minutes) {
    minutes /= 60.0;

    minutes = minutes.toFixed(0);

    if(minutes % 60 === 0)
        return `${minutes / 60}h`

    if(minutes < 60)
        return `${minutes}m`

    return `${Math.floor(minutes / 60)}h ${minutes % 60}m`;
}