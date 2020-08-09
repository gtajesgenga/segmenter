import React from "react";
import follow from "../client/follow";
import client from "../client/client";
import when from "when";
import {FilterList} from "./FilterList";
import {Button, Form, InputGroup} from "react-bootstrap";
import ReactDOM from "react-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";


const root = '/api';

export class FiltersView extends React.Component{

    constructor(props) {
        super(props);
        this.state = { pipelines: [], filters: [], selectedPipeline: undefined, selectedId: this.props.selectedId};
        this.addFilter = this.addFilter.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onDelete = this.onDelete.bind(this);
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
            this.getAvailableFilters();
        })
    }

    getAvailableFilters() {
        client({
            method: 'GET',
            path: '/api/filters'
        }).done(response => {
            this.setState({filters: response.entity._embedded.filters})
        });
    }

    componentDidMount() {
        this.loadFromServer();
    }

    onDelete(index) {
        this.state.selectedPipeline.entity.filters.splice(index, 1);

        client({
            method: 'PUT',
            path: this.state.selectedPipeline.entity._links.self.href,
            entity: this.state.selectedPipeline.entity,
            headers: {
                'Content-Type': 'application/json'
            }
        }).done(() => {
            this.loadFromServer();
            this.props.showAlert('Methods', 'success', 'pipeline ' + this.state.selectedPipeline.entity.id + ' was updated.');
        }, response => {
            if (response.status.code === 412) {
                this.props.showAlert('Methods', 'danger', 'DENIED: Unable to update ' + this.state.selectedPipeline.entity.id + '. Your copy is stale.');
            }
        });
    }

    onUpdate(pipeline) {
        client({
            method: 'PUT',
            path: pipeline.entity._links.self.href,
            entity: pipeline.entity,
            headers: {
                'Content-Type': 'application/json'
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

    addFilter(e) {
        e.preventDefault();
        const selectedId = ReactDOM.findDOMNode(this.refs.filter).value;
        const filter = JSON.parse(JSON.stringify(this.state.filters[selectedId]));
        filter.methods = [];
        this.state.selectedPipeline.entity.filters.push(filter);
        this.onUpdate(this.state.selectedPipeline);
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
        const filters = this.state.filters.map((filter, index) =>
            <option key={filter.filterClass.split(".").pop() + index} value={index}>{filter.filterClass.split(".").pop()}</option>
        );

        return (
            <div>
                <Form inline className={"mb-3 d-flex-inline justify-content-end float-left"}>
                    <InputGroup.Prepend>
                        <Form.Label htmlFor="pipelineSelect" column={"sm"} className={"pl-0 pr-1"}>Pipeline:</Form.Label>
                    </InputGroup.Prepend>
                    <Form.Control as={'select'}  size="sm" id="pipelineSelect" ref="pipeline" value={this.state.selectedId} onChange={this.handleChange} className={"mx-1"}>
                        {pipelines}
                    </Form.Control>
                </Form>
                <Form inline className={"mb-3 d-flex-inline justify-content-end float-right"}>
                    <InputGroup.Prepend>
                        <Form.Label htmlFor="filterSelect" column={"sm"} className={"pl-0 pr-1"}>Filters:</Form.Label>
                    </InputGroup.Prepend>
                    <Form.Control as={'select'}  size="sm" id="filterSelect" ref="filter" className={"mx-1"}>
                        {filters}
                    </Form.Control>
                    <Button variant={"success"} size={'sm'} onClick={this.addFilter}><FontAwesomeIcon icon={faPlus}/>&nbsp;Add</Button>
                </Form>
                <FilterList selectedPipeline={this.state.selectedPipeline} onUpdate={this.onUpdate} onDelete={this.onDelete}/>
            </div>
        );
    }
}
