import React from "react";
import {Container, Table, Button} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {Filter} from "./Filter";

export class FilterList extends React.Component{

    constructor (props) {
        super(props);
        this.handleOrder = this.handleOrder.bind(this);
    }

    handleOrder(filter, direction) {

    }

    render() {
        const filters = this.props.selectedPipeline !== undefined ? this.props.selectedPipeline.entity.filters.map((filter, index) => {
            let orderActions = <></>;
            switch (index) {
                case 0:
                    orderActions = <Button variant={"outline-primary"} className={"border-0"} size={'sm'} onClick={this.handleOrder(filter.uuid, 1)}><FontAwesomeIcon icon={fai.faSortDown} className={"align-text-top"} /></Button>;
                    break;
                case (this.props.selectedPipeline.entity.filters.length - 1) :
                    orderActions = <Button variant={"outline-primary"} size={'sm'} className={"border-0"} onClick={this.handleOrder(filter.uuid, -1)}><FontAwesomeIcon icon={fai.faSortUp} className={"align-text-bottom"} /></Button>;
                    break;
                default:
                    orderActions =
                        <>
                            <Button variant={"outline-primary"} size={'sm'} className={"border-0"} onClick={this.handleOrder(filter.uuid, 1)}><FontAwesomeIcon icon={fai.faSortDown} className={"align-text-top"}/></Button>
                            <>&nbsp;</>
                            <Button variant={"outline-primary"} size={'sm'} className={"border-0"} onClick={this.handleOrder(filter.uuid, -1)}><FontAwesomeIcon icon={fai.faSortUp} className={"align-text-bottom"}/></Button>
                        </>;
                    break;
            }

            return (
                <Filter key={filter.uuid} order={index} filter={filter} orderActions={orderActions}/>
            );
        }) : undefined;

        return (
            <Container>
                <Table striped bordered hover>
                    <thead className="thead-light">
                    <tr>
                        <th>Order</th>
                        <th>Name</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                        {filters}
                    </tbody>
                </Table>
            </Container>
        );
    }

}
