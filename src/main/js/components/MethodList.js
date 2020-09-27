import React from "react";
import {Container, Table} from "react-bootstrap";
import {Method} from "./Method";


export class MethodList extends React.Component{

    constructor(props) {
        super(props);
        this.onUpdate = this.onUpdate.bind(this);
    }

    onUpdate() {
        this.props.onUpdate(this.props.selectedFilter);
    }

    render() {
        const methods = this.props.selectedFilter !== undefined ? this.props.selectedFilter.methods.map((method, index) => {
            return (
                <Method key={method.name} index={index} method={method} filter={this.props.selectedFilter} onDelete={this.props.onDelete} onUpdate={this.onUpdate} />
            );
        }) : undefined;

        return (
            <Container>
                <Table striped bordered hover>
                    <thead className="thead-light">
                    <tr>
                        <th>Name</th>
                        <th>Value</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                        {methods}
                    </tbody>
                </Table>
            </Container>
        );
    }
}
