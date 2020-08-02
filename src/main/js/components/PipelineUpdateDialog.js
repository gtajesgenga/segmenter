

// tag::pipeline-update-dialog[]
import React from "react";
import ReactDOM from "react-dom";
import {MyModal} from "../utils/Utils";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {Form} from "react-bootstrap"

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
        window.location = '#'
    }

    render () {
        const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
            <Form key={attribute}>
                <Form.Label column={false}>{attribute}</Form.Label>
                <Form.Control type={'text'} placeholder={attribute} defaultValue={this.props.pipeline.entity[attribute]} ref={attribute} />
            </Form>
        );

        return (
            <MyModal customClass={'mr-1 float-left'} inputs={inputs} callback={this.handleSubmit} title={'Update pipeline'} btnLabel={'Update'} btnIcon={fai.faEdit}/>
        )
    }
}
// end::pipeline-update-dialog[]
