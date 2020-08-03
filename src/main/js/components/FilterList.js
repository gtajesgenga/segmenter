import React from "react";
import {Container, Table, Button} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import * as fai from "@fortawesome/free-solid-svg-icons";
import {Filter} from "./Filter";
import 'array.prototype.move';

export class FilterList extends React.Component{

    constructor (props) {
        super(props);
        this.handleOrder = this.handleOrder.bind(this);
    }

    handleOrder(e, filter, direction) {
        e.preventDefault();
        let idx = this.props.selectedPipeline.entity.filters.findIndex((f) => f.uuid === filter);

        if (idx > -1) {
            let moved = false;

            if ((direction > 0 && idx < this.props.selectedPipeline.entity.filters.length - 1) || (direction < 0 && idx > 0)) {
                this.props.selectedPipeline.entity.filters.move(idx, idx + direction);
                moved = true;
            }

            if (moved) {
                this.props.onUpdate(this.props.selectedPipeline);
            }
        }
    }

    render() {
        const filters = this.props.selectedPipeline !== undefined ? this.props.selectedPipeline.entity.filters.map((filter, index) => {
            let orderActions = <></>;
            switch (index) {
                case 0:
                    orderActions = <Button variant={"outline-primary"} className={"border-0"} size={'sm'} onClick={(e) => this.handleOrder(e, filter.uuid, 1)}><FontAwesomeIcon icon={fai.faSortDown} className={"align-text-top"}/></Button>;
                    break;
                case (this.props.selectedPipeline.entity.filters.length - 1) :
                    orderActions = <Button variant={"outline-primary"} size={'sm'} className={"border-0"} onClick={(e) => this.handleOrder(e, filter.uuid, -1)}><FontAwesomeIcon icon={fai.faSortUp} className={"align-text-bottom"}/></Button>;
                    break;
                default:
                    orderActions =
                        <>
                            <Button variant={"outline-primary"} size={'sm'} className={"border-0"} onClick={(e) => this.handleOrder(e, filter.uuid, 1)}><FontAwesomeIcon icon={fai.faSortDown} className={"align-text-top"}/></Button>
                            <>&nbsp;</>
                            <Button variant={"outline-primary"} size={'sm'} className={"border-0"} onClick={(e) => this.handleOrder(e, filter.uuid, -1)}><FontAwesomeIcon icon={fai.faSortUp} className={"align-text-bottom"}/></Button>
                        </>;
                    break;
            }

            return (
                <Filter key={filter.uuid} pipeline={this.props.selectedPipeline} order={index} filter={filter} orderActions={orderActions}/>
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
