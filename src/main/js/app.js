import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const follow = require('./follow'); // function to hop multiple links by "rel"
// end::vars[]

const rootPipelines = '/pipelineEntities';


// tag::app[]
class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {pipelines: [], attributes: [], links: {}};
    }

    loadFromServer() {
        var _schema;

        follow(client, rootPipelines, [
            {rel: 'pipelineEntities'}
        ], false).then(pipelineCollection => {
            return client({
                method: 'GET',
                path: pipelineCollection.entity._links.profile.href,
                headers: {'Accept': 'application/schema+json'}
            }).then(schema => {
                _schema = schema.entity;
                return pipelineCollection;
            });
        }).done(pipelineCollection => {
            this.setState({
                pipelines: pipelineCollection.entity._embedded.pipelineEntities,
                attributes: Object.keys(_schema.properties),
                links: pipelineCollection.entity._links
            });
        });
    }

    componentDidMount() {
        this.loadFromServer();
    }

    render() {
        return (
            <PipelineList pipelines={this.state.pipelines}/>
        )
    }
}

// end::app[]


// tag::pipeline-list[]
class PipelineList extends React.Component {
    render() {
        const pipelines = this.props.pipelines.map(pipeline =>
            <Pipeline key={pipeline._links.self.href} pipeline={pipeline}/>
        );
        return (
            <table className="table">
                <thead className="thead-light">
                <tr>
                    <th>Name</th>
                </tr>
                </thead>
                <tbody>
                {pipelines}
                </tbody>
            </table>
        )
    }
}
// end::pipeline-list[]

// tag::pipeline[]
class Pipeline extends React.Component{
    render() {
        return (
            <tr>
                <td>{this.props.pipeline.name}</td>
            </tr>
        )
    }
}
// end::pipeline[]

// tag::render[]
ReactDOM.render(
    <App />,
    document.getElementById('react')
);
// end::render[]