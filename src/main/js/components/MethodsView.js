import React from "react";
import client from "../client/client";
import {Form, InputGroup} from "react-bootstrap";
import ReactDOM from "react-dom";
import {MethodList} from "./MethodList";

const root = '/api/pipelines/';


export class MethodsView extends React.Component{

    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.state = {pipeline: undefined, filters: [], methods: [], selectedFilter: undefined, selectedUuid: undefined};
    }

    loadFromServer() {

        client({
            method: 'GET',
            path: root + this.props.selectedPipelineId
        }).done(pipeline => {
            let selectedFilter = pipeline.entity.filters.filter(filter => filter.uuid === this.props.selectedFilterUuid)[0];
            this.getAvailableMethods(selectedFilter.filterClass);
            this.setState({
                pipeline: pipeline,
                filters: pipeline.entity.filters,
                selectedFilter: selectedFilter,
                selectedUuid: selectedFilter.uuid
            });
        })
    }

    handleChange(e) {
        e.preventDefault();
        const selectedUuid = ReactDOM.findDOMNode(this.refs.filter).value;
        const selectedLabel = ReactDOM.findDOMNode(this.refs.filter).selectedOptions[0].text;
        let selectedFilter = this.state.pipeline.entity.filters.filter(filter => filter.uuid === selectedUuid)[0];
        this.getAvailableMethods(selectedLabel);
        this.setState({selectedFilter: selectedFilter, selectedUuid: selectedFilter.uuid});
    }

    getAvailableMethods(selectedLabel) {
        client({
            method: 'GET',
            path: '/api/filters/' + selectedLabel
        }).done(response => {
            this.setState({methods: response.data.methods})
        });
    }

    onUpdate() {
        client({
            method: 'PUT',
            path: this.state.pipeline.entity._links.self.href,
            entity: this.state.pipeline.entity,
            headers: {
                'Content-Type': 'application/json',
                'If-Match': this.state.pipeline.headers.Etag
            }
        }).done(() => {
            this.loadFromServer();
            this.props.showAlert('Filters', 'success', 'pipeline ' + this.state.pipeline.entity.id + ' was updated.');
        }, response => {
            if (response.status.code === 412) {
                this.props.showAlert('Filters', 'danger', 'DENIED: Unable to update ' + this.state.pipeline.entity.id + '. Your copy is stale.');
            }
        });
    }

    onDelete() {
        client({
            method: 'PUT',
            path: this.state.pipeline.entity._links.self.href,
            entity: this.state.pipeline.entity,
            headers: {
                'Content-Type': 'application/json',
                'If-Match': this.state.pipeline.headers.Etag
            }
        }).done(() => {
            this.loadFromServer();
            this.props.showAlert('Methods', 'success', 'pipeline ' + this.state.pipeline.entity.id + ' was updated.');
        }, response => {
            if (response.status.code === 412) {
                this.props.showAlert('Methods', 'danger', 'DENIED: Unable to update ' + this.state.pipeline.entity.id + '. Your copy is stale.');
            }
        });
    }

    componentDidMount() {
        this.loadFromServer();
    }

    render() {
        const filters = this.state.filters.map(filter =>
            <option key={filter.uuid} value={filter.uuid}>{filter.filterClass.split(".").pop()}</option>
        );
        const methods = this.state.methods.map((method, index) =>
            <option key={method.name + index} value={index}>{method.name}</option>
        );

        return (
            <div>
                <Form inline className={"mb-3 d-flex justify-content-end"}>
                    <InputGroup.Prepend>
                        <Form.Label htmlFor="methodSelect" column={"sm"} className={"pl-0 pr-1"}>Methods:</Form.Label>
                    </InputGroup.Prepend>
                    <Form.Control as={'select'}  size="sm" id="methodSelect" ref="method">
                        {methods}
                    </Form.Control>
                </Form>
                <Form inline className={"mb-3 d-flex justify-content-end"}>
                    <InputGroup.Prepend>
                        <Form.Label htmlFor="filterSelect" column={"sm"} className={"pl-0 pr-1"}>Filter:</Form.Label>
                    </InputGroup.Prepend>
                    <Form.Control as={'select'}  size="sm" id="filterSelect" ref="filter" value={this.state.selectedUuid} onChange={this.handleChange}>
                        {filters}
                    </Form.Control>
                </Form>
                <MethodList selectedFilter={this.state.selectedFilter} onUpdate={this.onUpdate} onDelete={this.onDelete} onUpdate={this.onUpdate}/>
            </div>
        );
    }
}
