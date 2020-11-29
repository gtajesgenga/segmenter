

// tag::pipeline-update-dialog[]
import React from "react";
import ReactDOM from "react-dom";
import {CustomModal} from "../utils/Utils";
import {Form} from "react-bootstrap"
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import FormCheckInput from "react-bootstrap/FormCheckInput";

export class MethodUpdateDialog extends React.Component {
    constructor (props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit (e) {
        e.preventDefault();
        this.props.method.parameters.forEach((parameter, index) => {
            let node = ReactDOM.findDOMNode(this.refs[this.props.method.name + "." + index])

            switch (node.type) {
                case 'checkbox':
                    parameter.value = node.checked;
                    break;
                default:
                    parameter.value = node.value.trim()
                    break;
            }
        });
        this.props.onUpdate(this.props.method);
    }

    render () {
        const inputs = this.props.method.parameters.map((parameter, index) => {
            let fieldType = 'text';
            let selectOptions = (<></>);
            let placeholder = parameter.casting.split(".").pop();
            let min_ = 0;
            let max_ = 100;
            let step = 1;

            if (parameter.multidimensional !== undefined) {
                placeholder = 'tern numeric value [x,y,z] or percent: [x%,y%,z%]';
            } else {
                if (placeholder === 'Boolean') {
                    fieldType = 'checkbox';
                } else if (parameter.hasValues) {
                        fieldType = 'select';
                    selectOptions = Object.keys(parameter.values).map((_key, _index) => {
                        return (<option value={parameter.values[_key]} key={_key}>{_key}</option>);
                    });
                    return (
                        <Form key={this.props.method.name + "." + index}>
                            <Form.Label column={false}>{parameter.name}</Form.Label>
                            <Form.Control as={fieldType} placeholder={placeholder} defaultValue={parameter.value} ref={this.props.method.name + "." + index}>
                                {selectOptions}
                            </Form.Control>
                        </Form>
                    );
                } else {
                    fieldType = 'number';

                    switch (placeholder) {
                        case 'Short':
                            min_ = -32768;
                            max_ = 32767;
                            break;
                        case 'Int':
                            min_ = -2147483648;
                            max_ = 2147483647;
                            break;
                        case 'Long':
                            min_ = -922337203680000;
                            max_ = 922337203680000;
                            break;
                        case 'Float':
                            min_ = -340282346638528;
                            max_ = 340282346638528;
                            step = 0.1;
                            break;
                        case 'Double':
                            min_ = -179769313486231;
                            max_ = 179769313486231;
                            step = 0.1;
                            break;
                    }
                }
            }

            return (
                <Form key={this.props.method.name + "." + index}>
                    <Form.Label column={false}>{parameter.name}</Form.Label>
                    <Form.Control type={fieldType}
                                  placeholder={placeholder}
                                  min={min_}
                                  max={max_}
                                  step={step}
                                  defaultChecked={fieldType === 'checkbox' && parameter.value === 'true'}
                                  defaultValue={parameter.value}
                                  ref={this.props.method.name + "." + index}/>
                </Form>
            );
        });

        return (
            <CustomModal customClass={'mr-1 float-left'} variant={'primary'} content={inputs} callback={this.handleSubmit} title={'Edit pipeline'} btnLabel={'Edit'} acceptBtnLabel={"Update"} btnIcon={faEdit}/>
        )
    }
}
// end::pipeline-update-dialog[]
