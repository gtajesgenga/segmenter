import * as fai from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {CustomModal} from "../utils/Utils";
import {Form} from "react-bootstrap";
import {MethodUpdateDialog} from "./MethodUpdateDialog";


export class Method extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete(e) {
        e.preventDefault();
        this.props.filter.methods.splice(this.props.index, 1);
        this.props.onDelete(this.props.filter);
    }

    render() {
        let parameters = this.props.method.parameters.reduce((str, parameter) => { return str + parameter.value + ","; }, "");

        return(
            <tr>
                <td>{this.props.method.name}</td>
                <td>{parameters}</td>
                <td>
                    <MethodUpdateDialog method={this.props.method} onUpdate={this.props.onUpdate}/>
                    <CustomModal customClass={"float-left"} mode={"delete"} variant={"danger"} btnIcon={fai.faTrash} btnLabel={"Delete"} title={"Delete method"}
                                 content={
                                     <>
                                         <Form.Text className="text-muted">
                                             Do you want to remove the method <b>{this.props.method.name}</b>?
                                         </Form.Text>
                                     </>}
                                 callback={this.handleDelete}
                    />
                </td>
            </tr>
        );
    }

}
