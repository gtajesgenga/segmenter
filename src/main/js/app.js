// tag::imports[]
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import '../resources/static/main.css'
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import * as fai from '@fortawesome/free-solid-svg-icons'
import Alert from 'react-bootstrap/Alert'
import Button from 'react-bootstrap/Button'
import Modal from 'react-bootstrap/Modal'
import Form from 'react-bootstrap/Form'
import React, {useState} from 'react'
import FormControl from 'react-bootstrap/FormControl'
import FormLabel from 'react-bootstrap/FormLabel'
import {Container, Nav, Pagination, Table} from 'react-bootstrap'
import follow from "./client/follow";
import ReactDOM from "react-dom";
import client from "./client/client";
import when from "when";
// end::imports[]

const root = '/api';

function MyModal (props) {
  const [show, setShow] = useState(false);
  const {btnIcon, inputs, customClass, title, btnLabel} = props;

  const icon = btnIcon !== undefined ? <FontAwesomeIcon icon={btnIcon} size={'sm'}/> : <></>;

  return (
    <>
      <div className={customClass}>
        <Button className={'float-right'} variant={'success'} size={'sm'} onClick={() => setShow(true)}>
          {icon}&nbsp;{btnLabel}
        </Button>

        <Modal show={show} onHide={() => setShow(false)}>
          <Modal.Header closeButton>
            <Modal.Title>{title}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {inputs}
          </Modal.Body>
          <Modal.Footer>
            <Button variant={'primary'} onClick={(e) => { setShow(false); props.callback(e) }}>{btnLabel}</Button>
          </Modal.Footer>
        </Modal>
      </div>
    </>
  )
}

// tag::app[]
class App extends React.Component {
  constructor (props) {
    super(props);
    this.state = { pipelines: [], attributes: [], pageSize: 5, links: {}, showAlert: false, alertVariant: 'success', alertText: '' };
    this.updatePageSize = this.updatePageSize.bind(this);
    this.onCreate = this.onCreate.bind(this);
    this.onUpdate = this.onUpdate.bind(this);
    this.onDelete = this.onDelete.bind(this);
    this.onNavigate = this.onNavigate.bind(this)
  }

  showAlert () {
    this.setState({
      showAlert: true
    });

    setTimeout(() => {
      this.setState({
        showAlert: false
      })
    }, 5000)
  }

  loadFromServer (pageSize) {
    var _schema;

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
        this.onNavigate(response.entity._links.last.href)
      } else {
        this.onNavigate(response.entity._links.self.href)
      }
      this.setState({
        alertVariant: 'success',
        alertText: 'Pipeline ' + newPipeline.name + ' created!.'
      });
      this.showAlert()
    }, response => {
      if (response.status.code !== 201) {
        this.setState({
          alertVariant: 'danger',
          alertText: 'ERROR: Unable to create ' + newPipeline.name + '.'
        });
        this.showAlert()
      }
    })
  }
  // end::create[]

  // tag::update[]
  onUpdate (pipeline, updatedPipeline) {
    const alerts = this.state.alerts;
    client({
      method: 'PUT',
      path: pipeline.entity._links.self.href,
      entity: updatedPipeline,
      headers: {
        'Content-Type': 'application/json',
        'If-Match': pipeline.headers.Etag
      }
    }).done(() => {
      this.loadFromServer(this.state.pageSize)
    }, response => {
      if (response.status.code === 412) {
        this.setState({
          alertVariant: 'danger',
          alertText: 'DENIED: Unable to update ' + pipeline.entity._links.self.href + '. Your copy is stale.'
        });
        this.showAlert()
      }
    });
    this.setState({ alerts: alerts })
  }
  // end::update[]

  // tag::delete[]
  onDelete (pipeline) {
    client({ method: 'DELETE', path: pipeline.entity._links.self.href }).done(() => {
      this.loadFromServer(this.state.pageSize)
    })
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

  componentDidMount () {
    this.loadFromServer(this.state.pageSize)
  }

  render () {
    return (
      <div>
        <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({ showAlert: !this.state.showAlert })} dismissible>
          <p>
            {this.state.alertText}
          </p>
        </Alert>
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
    )
  }
}

// end::app[]

// tag::pipeline-list[]
class PipelineList extends React.Component {
  constructor (props) {
    super(props);
    this.handleNavFirst = this.handleNavFirst.bind(this);
    this.handleNavPrev = this.handleNavPrev.bind(this);
    this.handleNavNext = this.handleNavNext.bind(this);
    this.handleNavLast = this.handleNavLast.bind(this);
    this.handleInput = this.handleInput.bind(this)
  }

  // tag::handle-page-size-updates[]
  handleInput (e) {
    e.preventDefault();
    const pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
    if (/^[0-9]+$/.test(pageSize)) {
      this.props.updatePageSize(pageSize)
    } else {
      ReactDOM.findDOMNode(this.refs.pageSize).value =
                pageSize.substring(0, pageSize.length - 1)
    }
  }
  // end::handle-page-size-updates[]

  // tag::handle-nav[]
  handleNavFirst (e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.first.href)
  }

  handleNavPrev (e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.prev.href)
  }

  handleNavNext (e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.next.href)
  }

  handleNavLast (e) {
    e.preventDefault();
    this.props.onNavigate(this.props.links.last.href)
  }
  // end::handle-nav[]

  render () {
    const pipelines = this.props.pipelines.map(pipeline =>
      <Pipeline key={pipeline.entity._links.self.href}
        pipeline={pipeline} attributes={this.props.attributes}
        excludes={this.props.excludes}
        defaults={{ filters: pipeline.entity.filters, id: pipeline.entity.id }}
        onDelete={this.props.onDelete}
        onUpdate={this.props.onUpdate}/>
    );

    const navLinks = [];
    if ('first' in this.props.links) {
      navLinks.push(<Pagination.First className="page-item" key="li-first" onClick={this.handleNavFirst} href="#" />)
    }
    if ('prev' in this.props.links) {
      navLinks.push(<Pagination.Prev className="page-item" key="li-prev" onClick={this.handleNavPrev} href="#" />)
    }
    if ('next' in this.props.links) {
      navLinks.push(<Pagination.Next className="page-item" key="li-next" onClick={this.handleNavNext} href="#" />)
    }
    if ('last' in this.props.links) {
      navLinks.push(<Pagination.Last className="page-item" key="li-last" onClick={this.handleNavLast} href="#" />)
    }

    return (
      <Container>
        <div className="mb-3 d-flex">
          <div className="input-group-prepend">
            <FormLabel className="input-group-text" htmlFor="pageSelect" column={false}>Show:</FormLabel>
          </div>
          <FormControl as={'select'} className="col-1 custom-select" id="pageSelect" ref="pageSize" onChange={this.handleInput} value={this.props.pageSize}>
            <option value="1">1</option>
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </FormControl>
          <div className="input-group-append">
            <FormLabel column={false} className="input-group-text" htmlFor="pageSelect">items</FormLabel>
          </div>
        </div>
        <Table striped bordered hover>
          <thead className="thead-light">
            <tr>
              <th>Id</th>
              <th>Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {pipelines}
          </tbody>
        </Table>
        <Container>
          <Nav aria-label="Page navigation">
            <Pagination className="pagination">
              {navLinks}
            </Pagination>
          </Nav>
        </Container>
      </Container>
    )
  }
}
// end::pipeline-list[]

// tag::pipeline[]
class Pipeline extends React.Component {
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
        </td>
      </tr>
    )
  }
}
// end::pipeline[]

// tag::pipeline-create-dialog[]
class PipelineCreateDialog extends React.Component {
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

    // Navigate away from the dialog to hide it.
    window.location = '#'
  }

  render () {
    const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
      <Form key={attribute}>
        <FormLabel column={false}>{attribute}</FormLabel>
        <FormControl type={'text'} placeholder={attribute} ref={attribute} />
      </Form>
    );

    return (
      <MyModal inputs={inputs} customClass={'pr-3'} callback={this.handleSubmit} title={'Create new pipeline'} btnLabel={'Create'} btnIcon={fai.faPlus}/>
    )
  }
}
// end::pipeline-create-dialog[]

// tag::pipeline-update-dialog[]
class PipelineUpdateDialog extends React.Component {
  constructor (props) {
    super(props);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleSubmit (e) {
    e.preventDefault();
    const updatedPipeline = {};
    this.props.excludes.forEach(exclude => {
      if (this.props.defaults[exclude]) {
          updatedPipeline[exclude] = this.props.defaults[exclude]
      }
    });
    this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).forEach(attribute => {
      updatedPipeline[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim()
    });
    this.props.onUpdate(this.props.pipeline, updatedPipeline);
    window.location = '#'
  }

  render () {
    const inputs = this.props.attributes.filter(attribute => !this.props.excludes.includes(attribute)).map(attribute =>
      <Form key={attribute}>
        <FormLabel column={false}>{attribute}</FormLabel>
        <FormControl type={'text'} placeholder={attribute} defaultValue={this.props.pipeline.entity[attribute]} ref={attribute} />
      </Form>
    );

    return (
      <MyModal customClass={'mr-1 float-left'} inputs={inputs} callback={this.handleSubmit} title={'Update pipeline'} btnLabel={'Update'} btnIcon={fai.faEdit}/>
    )
  }
}
// end::pipeline-update-dialog[]

// tag::render[]
ReactDOM.render(
  <App />,
  document.getElementById('react')
);
// end::render[]
