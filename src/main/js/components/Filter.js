import React from "react";
import {Button, Form} from "react-bootstrap";
import {faTrash, faSlidersH} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {CustomModal} from "../utils/Utils";

export class Filter extends React.Component{

    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete(e) {
        e.preventDefault();
        this.props.onDelete(this.props.order);
    }

    render() {
        const simpleNAme = this.props.filter.filterClass.split(".").pop();
        return (
            <tr>
                <td>{this.props.order}</td>
                <td>{simpleNAme}</td>
                <td>
                    <CustomModal customClass={"float-left"} mode={"delete"} variant={"danger"} btnIcon={faTrash} btnLabel={"Delete"} title={"Delete filter"}
                                 content={
                                     <>
                                         <Form.Text className="text-muted">
                                             Do you want to remove the filter <b>{simpleNAme}</b>?
                                         </Form.Text>
                                     </>}
                                 callback={this.handleDelete}
                     />
                    <Button className={"mx-1"} size={"sm"} variant={"primary"} href={"/ui#/pipelines/" + this.props.pipeline.entity.id + "/filters/" + this.props.filter.uuid + "/methods"}><FontAwesomeIcon icon={faSlidersH} size={"sm"} />&nbsp;Methods</Button>
                    {this.props.orderActions}
                </td>
            </tr>
        );
    }
}
