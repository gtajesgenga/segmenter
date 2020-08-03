import React from "react";
import {Button} from "react-bootstrap";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export class Filter extends React.Component{

    constructor(props) {
        super(props);
    }

    render() {
        const simpleNAme = this.props.filter.filterClass.split(".").pop();
        return (
            <tr>
                <td>{this.props.order}</td>
                <td>{simpleNAme}</td>
                <td>
                    <Button className={"mr-1"} size={"sm"} variant={"primary"} href={"/ui#/pipelines/" + this.props.pipeline.entity.id + "/filters/" + this.props.filter.uuid + "/methods"}><FontAwesomeIcon icon={fai.faSlidersH} size={"sm"} />&nbsp;Methods</Button>
                    {this.props.orderActions}
                </td>
            </tr>
        );
    }
}
