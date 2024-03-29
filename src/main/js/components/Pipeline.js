

// tag::pipeline[]
import React from "react";
import {Button, Form} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {PipelineUpdateDialog} from "./PipelineUpdateDialog";
import {CustomModal} from "../utils/Utils";
import {faFilter} from "@fortawesome/free-solid-svg-icons";
import {faTrash} from "@fortawesome/free-solid-svg-icons";

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
                    <CustomModal customClass={"float-left"} mode={"delete"} variant={"danger"} btnIcon={faTrash} btnLabel={"Delete"} title={"Delete pipeline"}
                                 content={
                                     <>
                                         <Form.Text className="text-muted">
                                             Do you want to remove the pipeline <b>{this.props.pipeline.entity.name}</b>?
                                         </Form.Text>
                                     </>}
                                 callback={this.handleDelete}
                    />
                    <div className={"ml-1 d-inline-block"}>
                        <Button variant={'primary'} size={"sm"} href={"/ui#/pipelines/" + this.props.pipeline.entity.id + "/filters"}><FontAwesomeIcon
                        icon={faFilter}
                        size={"sm"}/>&nbsp;Filters</Button>
                    </div>
                </td>
            </tr>
        )
    }
}
// end::pipeline[]
