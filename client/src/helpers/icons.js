import back_icon from '../resources/back.svg'
import checkmark_icon from '../resources/checkmark.svg'
import logout_icon from '../resources/logout.svg'
import plus_icon from '../resources/plus.svg'
import search_icon from '../resources/search.svg'
import settings_icon from '../resources/settings.svg'
import sync_icon from '../resources/sync.svg'
import trash_icon from '../resources/trash.svg'
import x_icon from '../resources/x.svg'

export class Icon {
    alt_text

    icon

    constructor(text, img) {
        this.alt_text = text
        this.icon = img
    }
}

export const back = new Icon('Back Icon', back_icon)
export const checkmark = new Icon('Checkmark Icon', checkmark_icon)
export const logout = new Icon('Logout Icon', logout_icon)
export const plus = new Icon('Plus Icon', plus_icon)
export const search = new Icon('Search Icon', search_icon)
export const settings = new Icon('Settings Icon', settings_icon)
export const sync = new Icon('Sync Icon', sync_icon)
export const trash = new Icon('Trash Icon', trash_icon)
export const x = new Icon('X Icon', x_icon)