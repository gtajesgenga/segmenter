

// tag::pipeline-update-dialog[]
import React from "react";
import ReactDOM from "react-dom";
import {CustomModal} from "../utils/Utils";
import {Form} from "react-bootstrap"
import {faEdit} from "@fortawesome/free-solid-svg-icons";

export class PipelineUpdateDialog extends React.Component {
    constructor (props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit (e) {
        e.preventDefault();
        const updatedPipeline = {};
        this.props.excludes.forEach(exclude => {
            if (this.props.defaults[exclude]) {
                updatedPipeline[exclude] = this.props.defaults[exclude]
            }
        });
        this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).forEach(attribute => {
            updatedPipeline[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim()
        });
        this.props.onUpdate(this.props.pipeline, updatedPipeline);
    }

    render () {
        const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
            <Form key={attribute}>
                <Form.Label column={false}>{attribute}</Form.Label>
                <Form.Control type={'text'} placeholder={attribute} defaultValue={this.props.pipeline.entity[attribute]} ref={attribute} />
            </Form>
        );

        return (
            <CustomModal customClass={'mr-1 float-left'} content={inputs} variant={'primary'} callback={this.handleSubmit} title={'Edit pipeline'} btnLabel={'Edit'} acceptBtnLabel={"Update"} btnIcon={faEdit}/>
        )
    }
}
// end::pipeline-update-dialog[]
