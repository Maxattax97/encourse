import React, { Component } from 'react'
import {setModalState} from '../redux/actions'
import connect from 'react-redux/es/connect/connect'

export function elementFromProps(props, content) {
	return (
		props.h1 ? <h1>{content}</h1> : props.h2 ? <h2>{content}</h2> :
			props.h3 ? <h3>{content}</h3> : props.h4 ? <h4>{content}</h4> :
				props.h5 ? <h5>{content}</h5> : props.h6 ? <h6>{content}</h6> :
					<p>{content}</p>
    )
}

export class Title extends Component {
    render() {
        return (
            <div className={ `title${this.props.onClick ? ' action' : ''}` } onClick={ this.props.onClick || null }>
                { this.props.children }
            </div>
        )
    }
}

export class Card extends Component {
    render() {
        return (
            <div className={ this.props.className ? 'card ' + this.props.className : 'card' } onClick={ this.props.onClick || (() => {}) }>
                <div className="component">
                    { this.props.children }
                </div>
            </div>
        )
    }
}

class ModalClass extends Component {

    previousModal = 0

    close = () => {
        if(!this.props.onClose || this.props.onClose())
            this.props.closeModal()
    }

    render() {
        const show = this.props.modalState === this.props.id
        const openedModal = this.previousModal !== this.props.modalState
	    this.previousModal = this.props.modalState

        if(show && openedModal && this.props.onOpen)
            this.props.onOpen()

        return (
            <div className='modal' style={show ? {} : {display: 'none'}} onClick={ this.props.closeModal }>
                <div className={this.props.left ? 'modal-left' : this.props.right ? 'modal-right' : 'modal-center'} onClick={(e) => e.stopPropagation()}>
                    <Card>
                        <div className={'modal-container'}>
                            {this.props.children}
                        </div>
                        {
                            !this.props.noExit ?
	                            <div className="action svg-icon exit-nav" onClick={ this.close }>
		                            <XIcon/>
	                            </div>
                                : null
                        }
                    </Card>
                </div>
            </div>
        )
    }

    static mapStateToProps = (state) => {
        return {
            modalState: state.control && state.control.modalState ? state.control.modalState : 0,
        }
    }

    static mapDispatchToProps = (dispatch) => {
        return {
            closeModal: () => dispatch(setModalState(0)),
        }
    }
}

export const Modal = connect(ModalClass.mapStateToProps, ModalClass.mapDispatchToProps)(ModalClass)

export class Summary extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className={ `summary ${this.props.className ? this.props.className : ''}` }>
                <div className='summary-container'>
                    <div className={`float-height cols-${this.props.columns ? this.props.columns : '2'}`}>
                         { this.props.children }
                    </div>
                </div>
            </div>
        )
    }
}

export class Filter extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className='filter'>
                <div className='filter-container'>
                    { this.props.children }
                </div>
            </div>
        )
    }
}

export class BackNav extends Component {
    render() {
        return (
            <div className={ `top-nav back-nav svg-icon float-height${ this.props.backClick ? ' action' : '' }` } onClick={ this.props.backClick }>
                <h3>
                    { this.props.back }
                </h3>
                {
                    this.props.backClick ? <BackIcon/> : null
                }
            </div>
        )
    }
}

export class Checkbox extends Component {

    render() {
        return (
            <div className={ this.props.className ? 'checkbox ' + this.props.className : 'checkbox' } onClick={ this.props.onClick }>
	            <div className='checkbox-value'>
		            {
			            this.props.children
		            }
	            </div>
            </div>
        )
    }
}

export class Dropdown extends Component {

    constructor(props) {
        super(props)

        this.state = {
            show : false
        }
    }

    componentDidMount() {
        document.addEventListener('mousedown', this.clickEvent)
    }

    componentWillUnmount() {
        document.removeEventListener('mousedown', this.clickEvent)
    }

    setDropdownRef = (node) => {
        this.dropdown = node
    }

    clickEvent = (event) => {
        if(this.dropdown && this.state.show && !this.dropdown.contains(event.target))
            this.setState({ show: false })
    }

    render() {
        const index = this.props.currentIndex || 0

	    const DropdownHeader = React.createElement(this.props.header,
		    {},
		    this.props.left ?
			    this.props.text + ' ' + this.props.values[index] :
			    this.props.values[index] + ' ' + this.props.text
	    )

	    const options = this.props.values.map((item) =>
		    React.createElement(this.props.header,
			    {},
			    this.props.left ?
				    this.props.text + ' ' + item :
				    item + ' ' + this.props.text
		    )
	    )

        return (
            <div ref={ this.setDropdownRef } className='dropdown'>
                <div className='dropdown-toggle' onClick={ () => this.setState({ show: !this.state.show }) }>
                    {DropdownHeader}
                    <DropdownIcon/>
                </div>
                <ul className={ 'dropdown-menu ' + ( this.props.right ? 'dropdown-menu-right' : 'dropdown-menu-left' ) }
                    style={ this.state.show ? null : { display: 'none' } }>
                    {
                        React.Children.map(options, (child, index) =>
                            <li className='action' onClick={ () => {
                                this.props.onClick(index)
                                this.setState({ show: false })
                            } }>
                                { child }
                            </li>
                        )
                    }
                </ul>
            </div>
        )
    }
}

export class SVG extends Component {
    render() {
        return (
            <svg className={this.props.className}
                viewBox="0 0 32 32"
                xmlSpace="preserve"
                xmlns="http://www.w3.org/2000/svg" version="1.1" >
                {this.props.children}
            </svg>
        )
    }
}

export class BackIcon extends Component {
    render() {
        return (
            <SVG className={ this.props.className ? 'icon ' + this.props.className : 'icon' }>
                <polygon points="22,8.025 19.98125,6 10,16 10,16 10,16 19.98125,26 22,23.975 14.04375,16" />
            </SVG>
        )
    }
}

export class DropdownIcon extends Component {
    render() {
        return (
            <SVG className={ this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <polygon transform="rotate(-90 16,16) " points="22,8.025 19.98125,6 10,16 10,16 10,16 19.98125,26 22,23.975 14.04375,16" />
            </SVG>
        )
    }
}

export class CheckmarkIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <polygon points="27.047 2.667, 12.000 18.090, 4.952 11.409, 0.000 16.364, 12.000 28.001, 32.000 7.621"/>
            </SVG>
        )
    }
}

export class LogoutIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <path d="m21.978 23.417c.198.198 .447.298 .719.298s.521-.099.719-.298l6.698-6.698c.05-.05.099-.099.124-.149 0 0 .025-.025.025-.05.025-.05.05-.099.074-.124 0-.025 0-.05.025-.05.025-.05.025-.074.05-.124.025-.074.025-.124.025-.198 0-.074 0-.124-.025-.198 0-.05-.025-.099-.05-.124 0-.025 0-.05-.025-.05-.025-.05-.05-.099-.074-.149 0 0 0-.025-.025-.025-.025-.05-.074-.099-.124-.149l-6.698-6.698c-.397-.397-1.042-.397-1.439 0-.397.397-.397 1.042 0 1.439l4.961 4.961h-17.637c-.571 0-1.017.447-1.017 1.017 0 .571.447 1.017 1.017 1.017h17.637l-4.961 4.961c-.397.347-.397.992 0 1.389z"/>
                <path d="m2.605 30.388h13.395c.571 0 1.017-.447 1.017-1.017v-9.997c0-.571-.447-1.017-1.017-1.017s-1.017.447-1.017 1.017v8.98h-11.361v-24.732h11.361v8.98c0 .571.447 1.017 1.017 1.017s1.017-.447 1.017-1.017v-9.997c0-.571-.447-1.017-1.017-1.017h-13.395c-.571 0-1.017.447-1.017 1.017v26.766c.025.571 .471 1.017 1.017 1.017z"/>
            </SVG>
        )
    }
}

export class PlusIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <path d="M32 12.542H19.458V0H12.542v12.542H0v6.915h12.542v12.542h6.915V19.458h12.542V12.542z"/>
            </SVG>
        )
    }
}

export class SearchIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <polygon points="27.047 2.667, 12.000 18.090, 4.952 11.409, 0.000 16.364, 12.000 28.001, 32.000 7.621"/>
            </SVG>
        )
    }
}

export class SettingsIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <path d="M32 18.155v-4.309c-2.201-.783-3.592-1.003-4.292-2.692v-.001c-.703-1.695.133-2.845 1.129-4.943l-3.047-3.047c-2.081.989-3.244 1.833-4.943 1.129h-.001c-1.692-.701-1.913-2.101-2.692-4.292h-4.309c-.776 2.18-.999 3.589-2.692 4.292h-.001c-1.695.704-2.843-.131-4.943-1.129l-3.047 3.047c.993 2.091 1.833 3.245 1.129 4.943-.703 1.695-2.112 1.917-4.292 2.693v4.309c2.176.773 3.589.999 4.292 2.692.707 1.709-.152 2.888-1.129 4.943l3.047 3.048c2.083-.991 3.245-1.833 4.943-1.129h.001c1.693.701 1.915 2.105 2.692 4.292h4.309c.776-2.181 1-3.587 2.703-4.296h.001c1.683-.699 2.827.135 4.931 1.135l3.047-3.048c-.992-2.084-1.833-3.244-1.131-4.941.703-1.695 2.117-1.92 4.295-2.695zm-16 3.179c-2.945 0-5.333-2.388-5.333-5.333s2.388-5.333 5.333-5.333 5.333 2.388 5.333 5.333-2.388 5.333-5.333 5.333z"/>
            </SVG>
        )
    }
}

export class SyncIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <path d="M23.181 5.636l-1.55 1.675c3.309 2.169 5.231 6.144 4.578 10.299-.431 2.738-1.902 5.145-4.143 6.777-1.956 1.424-4.295 2.11-6.679 1.973l.41-.43c-.008 0-.017.001-.024 0l1.551-1.628-1.433-1.367-3.076 3.23-.001-.001-1.327 1.394 1.432 1.366.001 0 3.229 3.077 1.329-1.394-2.091-1.992c2.862.135 5.661-.696 8.009-2.406 2.728-1.986 4.519-4.916 5.044-8.249C29.209 13.036 27.014 8.319 23.181 5.636z"/>
                <path d="M5.792 14.391c.431-2.738 1.902-5.145 4.143-6.777 1.956-1.424 4.295-2.11 6.679-1.973l-.41.43c.008 0 .017-.001.024 0l-1.551 1.628 1.433 1.367 3.076-3.23.001 .001 1.327-1.394L19.083 3.076l-.001 0L15.854 0l-1.329 1.394 2.091 1.992c-2.862-.135-5.661.696-8.009 2.406-2.728 1.986-4.519 4.916-5.044 8.249C2.79 18.965 4.986 23.682 8.818 26.365l1.55-1.675C7.061 22.52 5.138 18.546 5.792 14.391z"/>
            </SVG>
        )
    }
}

export class TrashIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <polygon points="27.047 2.667, 12.000 18.090, 4.952 11.409, 0.000 16.364, 12.000 28.001, 32.000 7.621"/>
            </SVG>
        )
    }
}

export class XIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <path d="M32 4.667 27.333 0 16 11.333 4.667 0 0 4.667 11.333 16 0 27.333 4.667 32 16 20.667 27.333 32 32 27.333 20.667 16z"/>
            </SVG>
        )
    }
}

export class LoadingIcon extends Component {
    render() {
        return (
            <SVG className={this.props.className ? 'icon ' + this.props.className : 'icon'}>
                <path d='M16.161 4.135c-6.604 0-11.957 5.354-11.957 11.957h2.604c0-5.165 4.188-9.354 9.354-9.354V4.135z'>
                    <animateTransform attributeType="xml"
                        attributeName="transform"
                        type="rotate"
                        from="0 16 16"
                        to="360 16 16"
                        dur="0.6s"
                        repeatCount="indefinite"/>
                </path>
            </SVG>
        )
    }
}