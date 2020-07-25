import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
const follow = require('./follow'); // function to hop multiple links by "rel"
// end::vars[]

const root = '/api';


// tag::app[]
class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {pipelines: [], attributes: [], pageSize: 5, links: {}};
        this.updatePageSize = this.updatePageSize.bind(this);
        this.onCreate = this.onCreate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onNavigate = this.onNavigate.bind(this);
    }

    loadFromServer(pageSize) {
        var _schema;

        follow(client, root, [
            {rel: 'pipelines', params: {size: pageSize}}
        ]).then(pipelineCollection => {
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
                pipelines: pipelineCollection.entity._embedded.pipelines,
                attributes: Object.keys(_schema.properties),
                pageSize: pageSize,
                links: pipelineCollection.entity._links
            });
        });
    }

    // tag::create[]
    onCreate(newPipeline) {
        follow(client, root, ['pipelines']).then(pipelineCollection => {
            return client({
                method: 'POST',
                path: pipelineCollection.entity._links.self.href,
                entity: newPipeline,
                headers: {'Content-Type': 'application/json'}
            })
        }).then(response => {
            return follow(client, root, [
                {rel: 'pipelines', params: {'size': this.state.pageSize}}]);
        }).done(response => {
            if (typeof response.entity._links.last !== "undefined") {
                this.onNavigate(response.entity._links.last.href);
            } else {
                this.onNavigate(response.entity._links.self.href);
            }
        });
    }
    // end::create[]

    // tag::delete[]
    onDelete(pipeline) {
        client({method: 'DELETE', path: pipeline._links.self.href}).done(response => {
            this.loadFromServer(this.state.pageSize);
        });
    }
    // end::delete[]

    // tag::navigate[]
    onNavigate(navUri) {
        client({method: 'GET', path: navUri}).done(pipelineCollection => {
            this.setState({
                pipelines: pipelineCollection.entity._embedded.pipelines,
                attributes: this.state.attributes,
                pageSize: this.state.pageSize,
                links: pipelineCollection.entity._links
            });
        });
    }
    // end::navigate[]

    // tag::update-page-size[]
    updatePageSize(pageSize) {
        if (pageSize !== this.state.pageSize) {
            this.loadFromServer(pageSize);
        }
    }
    // end::update-page-size[]

    componentDidMount() {
        this.loadFromServer(this.state.pageSize);
    }

    render() {
        return (
            <div>
                <CreateDialog attributes={this.state.attributes} excludes={['filters']} defaults={{filters: []}} onCreate={this.onCreate}/>
                <PipelineList pipelines={this.state.pipelines}
                              links={this.state.links}
                              pageSize={this.state.pageSize}
                              onNavigate={this.onNavigate}
                              onDelete={this.onDelete}
                              updatePageSize={this.updatePageSize}/>
            </div>
        )
    }
}

// end::app[]


// tag::pipeline-list[]
class PipelineList extends React.Component {

    constructor(props) {
        super(props);
        this.handleNavFirst = this.handleNavFirst.bind(this);
        this.handleNavPrev = this.handleNavPrev.bind(this);
        this.handleNavNext = this.handleNavNext.bind(this);
        this.handleNavLast = this.handleNavLast.bind(this);
        this.handleInput = this.handleInput.bind(this);
    }

    // tag::handle-page-size-updates[]
    handleInput(e) {
        e.preventDefault();
        const pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
        if (/^[0-9]+$/.test(pageSize)) {
            this.props.updatePageSize(pageSize);
        } else {
            ReactDOM.findDOMNode(this.refs.pageSize).value =
                pageSize.substring(0, pageSize.length - 1);
        }
    }
    // end::handle-page-size-updates[]

    // tag::handle-nav[]
    handleNavFirst(e){
        e.preventDefault();
        this.props.onNavigate(this.props.links.first.href);
    }

    handleNavPrev(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.prev.href);
    }

    handleNavNext(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.next.href);
    }

    handleNavLast(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.last.href);
    }
    // end::handle-nav[]

    render() {
        const pipelines = this.props.pipelines.map(pipeline =>
            <Pipeline key={pipeline._links.self.href} pipeline={pipeline} onDelete={this.props.onDelete}/>
        );

        const navLinks = [];
        if ("first" in this.props.links) {
            navLinks.push(<li className="page-item" key="li-first"><a className="page-link" key="first" onClick={this.handleNavFirst} href="#">First</a></li>);
        }
        if ("prev" in this.props.links) {
            navLinks.push(<li className="page-item" key="li-prev"><a className="page-link" key="prev" onClick={this.handleNavPrev} href="#">Prev</a></li>);
        }
        if ("next" in this.props.links) {
            navLinks.push(<li className="page-item" key="li-next"><a className="page-link" key="next" onClick={this.handleNavNext} href="#">Next</a></li>);
        }
        if ("last" in this.props.links) {
            navLinks.push(<li className="page-item" key="li-last"><a className="page-link" key="last" onClick={this.handleNavLast} href="#">Last</a></li>);
        }

        return (
            <div>
                <div className="input-group mb-3">
                    <div className="input-group-prepend">
                        <label className="input-group-text" htmlFor="pageSelect">Show:</label>
                    </div>
                    <select className="col-1 custom-select" id="pageSelect" ref="pageSize" onChange={this.handleInput} value={this.props.pageSize}>
                        <option value="1">1</option>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                    </select>
                    <div className="input-group-append">
                        <label className="input-group-text" htmlFor="pageSelect">items</label>
                    </div>
                </div>
                <table className="table table-striped table-hover table-bordered">
                    <thead className="thead-light">
                    <tr>
                        <th>Name</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                        {pipelines}
                    </tbody>
                </table>
                <div>
                    <nav aria-label="Page navigation">
                        <ul className="pagination">
                            {navLinks}
                        </ul>
                    </nav>
                </div>
            </div>
        )
    }
}
// end::pipeline-list[]

// tag::pipeline[]
class Pipeline extends React.Component{

    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onDelete(this.props.pipeline);
    }

    render() {
        return (
            <tr>
                <td>{this.props.pipeline.name}</td>
                <td>
                    <button type="button" className="btn btn-sm btn-danger" onClick={this.handleDelete}>Delete</button>
                </td>
            </tr>
        )
    }
}
// end::pipeline[]

// tag::create-dialog[]
class CreateDialog extends React.Component {

    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {
        e.preventDefault();
        const newPipeline = {};
        this.props.excludes.forEach(exclude => {
            this.props.defaults[exclude] ? newPipeline[exclude] = this.props.defaults[exclude] : undefined;
        });
        this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).forEach(attribute => {
            newPipeline[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
        });
        this.props.onCreate(newPipeline);

        // clear out the dialog's inputs
        this.props.attributes.forEach(attribute => {
            ReactDOM.findDOMNode(this.refs[attribute]).value = '';
        });

        // Navigate away from the dialog to hide it.
        window.location = "#";
    }

    render() {
        const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
            <p key={attribute}>
                <input type="text" placeholder={attribute} ref={attribute} className="field"/>
            </p>
        );

        return (
            <div>
                <a href="#createPipeline" className="float-right btn btn-sm btn-success" data-toggle="modal" data-target="#createPipeline">Create</a>

                <div id="createPipeline" className="modal fade" tabIndex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div>
                                <div className="modal-header">
                                    <h2 className="modal-title" id="modalTitle">Create new pipeline</h2>
                                    <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>

                                <div className="modal-body">
                                    <form>
                                        {inputs}
                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-primary" onClick={this.handleSubmit}>Create</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}
// end::create-dialog[]

// tag::render[]
ReactDOM.render(
    <App />,
    document.getElementById('react')
);
// end::render[]
