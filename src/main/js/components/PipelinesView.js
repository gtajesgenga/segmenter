import React from "react";
import follow from "../client/follow";
import client from "../client/client";
import when from "when";
import {PipelineCreateDialog} from "./PipelineCreateDialog";
import {PipelineList} from "./PipelineList";

const root = '/api';

export class PipelinesView extends React.Component{

    constructor(props) {
        super(props);
        this.state = { pipelines: [], attributes: [], pageSize: 5, links: {} };
        this.updatePageSize = this.updatePageSize.bind(this);
        this.onCreate = this.onCreate.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onNavigate = this.onNavigate.bind(this);
    }

    // tag::create[]
    onCreate (newPipeline) {
        follow(client, root, ['pipelines']).then(pipelineCollection => {
            return client({
                method: 'POST',
                path: pipelineCollection.entity._links.self.href,
                entity: newPipeline,
                headers: { 'Content-Type': 'application/json' }
            })
        }).then(() => {
            return follow(client, root, [
                { rel: 'pipelines', params: { size: this.state.pageSize } }])
        }).done(response => {
            if (typeof response.entity._links.last !== 'undefined') {
                this.onNavigate(response.entity._links.last.href);
            } else {
                this.onNavigate(response.entity._links.self.href);
            }
            this.props.showAlert('Pipelines', 'success', 'Pipeline ' + newPipeline.name + ' created!.');
        }, response => {
            if (response.status.code !== 201) {
                this.props.showAlert('Pipelines', 'danger', 'ERROR: Unable to create ' + newPipeline.name + '.');
            }
        })
    }
    // end::create[]

    // tag::update[]
    onUpdate (pipeline, updatedPipeline) {
        client({
            method: 'PUT',
            path: pipeline.entity._links.self.href,
            entity: updatedPipeline,
            headers: {
                'Content-Type': 'application/json'
            }
        }).done(() => {
            this.loadFromServer(this.state.pageSize);
            this.props.showAlert('Pipelines', 'success', 'pipeline ' + pipeline.entity.id + ' was updated.');
        }, response => {
            if (response.status.code === 412) {
                this.props.showAlert('Pipelines', 'danger', 'DENIED: Unable to update ' + pipeline.entity.id + '. Your copy is stale.');
            }
        });
    }
    // end::update[]

    // tag::delete[]
    onDelete (pipeline) {
        client({ method: 'DELETE', path: pipeline.entity._links.self.href }).done(() => {
            this.loadFromServer(this.state.pageSize);
            this.props.showAlert('Pipelines', 'success', 'pipeline ' + pipeline.entity.id + ' was removed.');
        }, response => {
            if (response.status.code !== 200) {
                this.props.showAlert('Pipelines', 'danger', 'ERROR: Unable to delete ' + pipeline.entity.id + '.');
            }
        });
    }
    // end::delete[]

    // tag::navigate[]
    onNavigate (navUri) {
        client({ method: 'GET', path: navUri }).then(pipelineCollection => {
            this.links = pipelineCollection.entity._links;

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
                pipelines: pipelines,
                attributes: this.state.attributes,
                pageSize: this.state.pageSize,
                links: this.links
            })
        })
    }
    // end::navigate[]

    // tag::update-page-size[]
    updatePageSize (pageSize) {
        if (pageSize !== this.state.pageSize) {
            this.loadFromServer(pageSize)
        }
    }
    // end::update-page-size[]

    loadFromServer (pageSize) {
        let _schema;

        follow(client, root, [
            { rel: 'pipelines', params: { size: pageSize } }
        ]).then(pipelineCollection => {
            return client({
                method: 'GET',
                path: pipelineCollection.entity._links.profile.href,
                headers: { Accept: 'application/schema+json' }
            }).then(schema => {
                _schema = schema.entity;
                this.links = pipelineCollection.entity._links;
                return pipelineCollection
            })
        }).then(pipelineCollection => {
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
                pipelines: pipelines,
                attributes: Object.keys(_schema.properties),
                pageSize: pageSize,
                links: this.links
            })
        })
    }

    componentDidMount () {
        this.loadFromServer(this.state.pageSize)
    }

    render() {
        return (
            <div>
                <PipelineCreateDialog attributes={this.state.attributes}
                                      excludes={['filters', 'id']}
                                      defaults={{ filters: [], id: undefined }}
                                      onCreate={this.onCreate}/>
                <PipelineList pipelines={this.state.pipelines}
                              attributes={this.state.attributes}
                              excludes={['id', 'filters']}
                              links={this.state.links}
                              pageSize={this.state.pageSize}
                              onNavigate={this.onNavigate}
                              onUpdate={this.onUpdate}
                              onDelete={this.onDelete}
                              updatePageSize={this.updatePageSize}/>
            </div>
        );
    }
}
