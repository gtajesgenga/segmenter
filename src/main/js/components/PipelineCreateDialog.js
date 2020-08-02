
// tag::pipeline-create-dialog[]
import React from "react";
import ReactDOM from "react-dom";
import {Form} from "react-bootstrap";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {MyModal} from "../utils/Utils";

export class PipelineCreateDialog extends React.Component {
    constructor (props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this)
    }

    handleSubmit (e) {
        e.preventDefault();
        const newPipeline = {};
        this.props.excludes.forEach(exclude => {
            if (this.props.defaults[exclude]) {
                newPipeline[exclude] = this.props.defaults[exclude]
            }
        });
        this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).forEach(attribute => {
            newPipeline[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim()
        });
        this.props.onCreate(newPipeline);

        // clear out the dialog's inputs
        this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).forEach(attribute => {
            ReactDOM.findDOMNode(this.refs[attribute]).value = ''
        });

        // Navigate away from the dialog to hide it.
        window.location = '#'
    }

    render () {
        const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
            <Form key={attribute}>
                <Form.Label column={false}>{attribute}</Form.Label>
                <Form.Control type={'text'} placeholder={attribute} ref={attribute} />
            </Form>
        );

        return (
            <MyModal inputs={inputs} customClass={'pr-3'} callback={this.handleSubmit} title={'Create new pipeline'} btnLabel={'Create'} btnIcon={fai.faPlus}/>
        )
    }
}
// end::pipeline-create-dialog[]
