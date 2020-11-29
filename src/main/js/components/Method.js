import React from "react";
import {CustomModal} from "../utils/Utils";
import {Form} from "react-bootstrap";
import {MethodUpdateDialog} from "./MethodUpdateDialog";
import {faTrash} from "@fortawesome/free-solid-svg-icons";


export class Method extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
        this.handleDelete = this.handleDelete.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
    }

    onUpdate(method) {
        this.props.filter.methods[this.props.index] = method;
        this.props.onUpdate(this.props.filter);
    }

    handleDelete(e) {
        e.preventDefault();
        this.props.filter.methods.splice(this.props.index, 1);
        this.props.onDelete(this.props.filter);
    }

    render() {
        let parameters = this.props.method.parameters.reduce((str, parameter) => {
            let _value = parameter.value;

            if (parameter.hasValues) {
                _value = Object.keys(parameter.values).find(key => parameter.values[key].toString() === _value);
            }
            return str + _value + ",";
            }, "");

        if (parameters.endsWith(",")) {
            parameters = parameters.substring(0, parameters.length -1)
        }

        return(
            <tr>
                <td>{this.props.method.name}</td>
                <td>{parameters}</td>
                <td>
                    <MethodUpdateDialog method={this.props.method} onUpdate={this.onUpdate}/>
                    <CustomModal customClass={"float-left"} mode={"delete"} variant={"danger"} btnIcon={faTrash} btnLabel={"Delete"} title={"Delete method"}
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
