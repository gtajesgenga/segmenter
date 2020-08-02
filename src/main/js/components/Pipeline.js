

// tag::pipeline[]
import React from "react";
import {Button} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {PipelineUpdateDialog} from "./PipelineUpdateDialog";

export class Pipeline extends React.Component {
    constructor (props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this)
    }

    handleDelete () {
        this.props.onDelete(this.props.pipeline)
    }

    render () {
        return (
            <tr>
                <td>{this.props.pipeline.entity.id}</td>
                <td>{this.props.pipeline.entity.name}</td>
                <td>
                    <PipelineUpdateDialog pipeline={this.props.pipeline}
                                          attributes={this.props.attributes}
                                          excludes={this.props.excludes}
                                          defaults={this.props.defaults}
                                          onUpdate={this.props.onUpdate}/>

                    <Button variant={'danger'} size={'sm'} onClick={this.handleDelete}><FontAwesomeIcon icon={fai.faTrash} size={'sm'}/>&nbsp;Delete</Button>
                    <div className={"ml-1 d-inline-block"}>
                        <Button variant={'primary'} size={"sm"} href={"/ui#/filters/" + this.props.pipeline.entity.id}><FontAwesomeIcon
                        icon={fai.faFilter}
                        size={"sm"}/>&nbsp;Filters</Button>
                    </div>
                </td>
            </tr>
        )
    }
}
// end::pipeline[]
