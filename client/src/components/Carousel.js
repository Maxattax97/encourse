import {Component} from "react";
import React from "react";
import Card from "./Card";
import exitIcon from "../img/x.svg";

class StudentPanel extends Component {

    render() {
        return (
            <div className="carousel">
                <div className={this.props.left ? "modal-left" : this.props.right ? "modal-right" : "modal-center"} onClick={(e) => e.stopPropagation()}>
                    <Card component={<div className={"modal-container"}>
                        {this.props.component}
                        <div className="exit-nav" onClick={this.props.onClose}>
                            <img src={exitIcon} alt="" />
                        </div>
                    </div>} />
                </div>
            </div>
        )
    }
}

export default StudentPanel
