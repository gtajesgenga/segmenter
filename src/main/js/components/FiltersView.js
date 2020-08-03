import React from "react";
import follow from "../client/follow";
import client from "../client/client";
import when from "when";
import {FilterList} from "./FilterList";
import {Form, InputGroup} from "react-bootstrap";
import ReactDOM from "react-dom";


const root = '/api';

export class FiltersView extends React.Component{

    constructor(props) {
        super(props);
        this.state = { pipelines: [], selectedPipeline: undefined, selectedId: this.props.selectedId};
        this.handleChange = this.handleChange.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.selectPipeline = this.selectPipeline.bind(this);
    }

    loadFromServer() {

        follow(client, root, [
            { rel: 'pipelines', params: { size: 0x7FFFFFFF } }
        ]).then(pipelineCollection => {
            return pipelineCollection.entity._embedded.pipelines.map(pipeline =>
                client({
                    method: 'GET',
                    path: pipeline._links.self.href
                })
            )
        }).then(pipelinePromises => {
            return when.all(pipelinePromises)
        }).done(pipelines => {
            this.setState({
                pipelines: pipelines
            });
            this.selectPipeline(this.props.selectedId);
        })
    }

    componentDidMount() {
        this.loadFromServer();
    }

    onUpdate(pipeline) {
        client({
            method: 'PUT',
            path: pipeline.entity._links.self.href,
            entity: pipeline.entity,
            headers: {
                'Content-Type': 'application/json',
                'If-Match': pipeline.headers.Etag
            }
        }).done(() => {
            this.loadFromServer();
            this.props.showAlert('Filters', 'success', 'pipeline ' + pipeline.entity.id + ' was updated.');
        }, response => {
            if (response.status.code === 412) {
                this.props.showAlert('Filters', 'danger', 'DENIED: Unable to update ' + pipeline.entity.id + '. Your copy is stale.');
            }
        });
    }

    handleChange(e) {
        e.preventDefault();
        const selectedPipeline = ReactDOM.findDOMNode(this.refs.pipeline).value;
        this.selectPipeline(selectedPipeline);
    }

    selectPipeline(selectedPipeline) {
        const selected = selectedPipeline !== undefined ? this.state.pipelines.filter(pipeline => pipeline.entity.id.toString() === selectedPipeline)[0] :
            this.state.pipelines[0];
        this.setState({selectedPipeline: selected, selectedId: selected !== undefined ? selected.entity.id : selected});
    }

    render() {
        const pipelines = this.state.pipelines.map(pipeline =>
            <option key={pipeline.entity._links.self.href} value={pipeline.entity.id}>{pipeline.entity.name}</option>
        );

        return (
            <div>
                <Form inline className={"mb-3 d-flex justify-content-end"}>
                    <InputGroup.Prepend>
                        <Form.Label htmlFor="pipelineSelect" column={"sm"} className={"pl-0 pr-1"}>Pipeline:</Form.Label>
                    </InputGroup.Prepend>
                    <Form.Control as={'select'}  size="sm" className="col-1" id="pipelineSelect" ref="pipeline" value={this.state.selectedId} onChange={this.handleChange}>
                        {pipelines}
                    </Form.Control>
                </Form>
                <FilterList selectedPipeline={this.state.selectedPipeline} onUpdate={this.onUpdate}/>
            </div>
        );
    }
}
