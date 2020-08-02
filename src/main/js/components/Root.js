import React from "react";
import {Container, Row} from "react-bootstrap";
import {Header} from "./Header";
import Alert from "react-bootstrap/Alert";

export class Root extends React.Component {

    constructor(props) {
        super(props);
        this.state = { showAlert: false, alertVariant: 'success', alertText: '' };
        this.showAlert = this.showAlert.bind(this);
    }

    showAlert(variant, text) {
        this.setState({
            alertVariant: variant,
            alertText: text,
            showAlert: true
        });

        setTimeout(() => {
            this.setState({
                showAlert: false
            })
        }, 5000)
    }

    render() {
        const children = React.Children.map(this.props.children, child => {
            return React.cloneElement(child, {showAlert: this.showAlert});
        });

        return (
            <Container>
                <Row>
                    <div className={"col-12"}>
                        <Header />
                    </div>
                </Row>
                <Row className={"mt-3"}>
                    <div className={"col-12 ml-auto mr-auto"}>
                        {children}
                    </div>
                </Row>
                <Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({ showAlert: !this.state.showAlert })} dismissible>
                    <p>
                        {this.state.alertText}
                    </p>
                </Alert>
            </Container>
        );
    }
}
