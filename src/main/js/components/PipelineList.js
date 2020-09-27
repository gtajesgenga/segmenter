

// tag::pipeline-list[]
import React from "react";
import ReactDOM from "react-dom";
import {Container, Pagination, Table, Form, Nav, InputGroup} from "react-bootstrap";
import {Pipeline} from "./Pipeline";

export class PipelineList extends React.Component {
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
                <Form inline className={"mb-1 d-flex"}>
                    <InputGroup.Prepend>
                        <Form.Label htmlFor="pageSelect" column={"sm"} className={"pl-0 pr-1"}>Show:</Form.Label>
                    </InputGroup.Prepend>
                    <Form.Control as={'select'}  size="sm" className="col-1" id="pageSelect" ref="pageSize" onChange={this.handleInput} value={this.props.pageSize}>
                        <option value="1">1</option>
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                    </Form.Control>
                    <InputGroup.Append>
                        <Form.Label htmlFor="pageSelect" column={"sm"}  className={"pl-1 pr-0"}>items</Form.Label>
                    </InputGroup.Append>
                </Form>
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
