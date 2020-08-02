import React from "react";

export class Filter extends React.Component{

    constructor(props) {
        super(props);
    }

    render() {
        const simpleNAme = this.props.filter.filterClass.split(".").pop();
        return (
            <tr>
                <td>{this.props.order}</td>
                <td>{simpleNAme}</td>
                <td>{this.props.orderActions}</td>
            </tr>
        );
    }
}
