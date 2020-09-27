
// tag::pipeline-create-dialog[]
import React from "react";
import ReactDOM from "react-dom";
import {Form} from "react-bootstrap";
import {CustomModal} from "../utils/Utils";
import {faPlus} from "@fortawesome/free-solid-svg-icons";

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
    }

    render () {
        const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
            <Form key={attribute}>
                <Form.Label column={false}>{attribute}</Form.Label>
                <Form.Control type={'text'} placeholder={attribute} ref={attribute} />
            </Form>
        );

        return (
            <CustomModal content={inputs} variant={'success'} customClass={'pr-3'} callback={this.handleSubmit} title={'Create new pipeline'} btnLabel={'Create'} accpetBtnLabel={'Create'} btnIcon={faPlus}/>
        )
    }
}
// end::pipeline-create-dialog[]
