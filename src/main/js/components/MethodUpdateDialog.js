

// tag::pipeline-update-dialog[]
import React from "react";
import ReactDOM from "react-dom";
import {CustomModal} from "../utils/Utils";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {Form} from "react-bootstrap"

export class MethodUpdateDialog extends React.Component {
    constructor (props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit (e) {
        e.preventDefault();
        this.props.method.parameters.forEach((parameter, index) => {
            parameter.value = ReactDOM.findDOMNode(this.refs[this.props.method.name + "." + index]).value.trim()
        });
        this.props.onUpdate();
    }

    render () {
        const inputs = this.props.method.parameters.map((parameter, index) =>
            <Form key={this.props.method.name + "." + index}>
                <Form.Label column={false}>{parameter.name}</Form.Label>
                <Form.Control type={'text'} placeholder={parameter.name} defaultValue={parameter.value} ref={this.props.method.name + "." + index} />
            </Form>
        );

        return (
            <CustomModal customClass={'mr-1 float-left'} content={inputs} callback={this.handleSubmit} title={'Edit pipeline'} btnLabel={'Edit'} acceptBtnLabel={"Update"} btnIcon={fai.faEdit}/>
        )
    }
}
// end::pipeline-update-dialog[]
