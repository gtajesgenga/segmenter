import React from "react";
import {Container, Row} from "react-bootstrap";
import {Header} from "./Header";
import Toast from "react-bootstrap/Toast";

export class Root extends React.Component {

    constructor(props) {
        super(props);
        this.state = { showAlert: false, alertVariant: 'd4edda', alertText: '', alertTrigger: '' };
        this.showAlert = this.showAlert.bind(this);
    }

    showAlert(trigger, variant, text) {
        this.setState({
            alertTrigger: trigger,
            alertVariant: variant,
            alertText: text,
            showAlert: true
        });
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
                <Toast className={"bg-" + this.state.alertVariant} show={this.state.showAlert} onClose={() => this.setState({ showAlert: !this.state.showAlert})} delay={5000} autohide>
                    <Toast.Header>
                        <strong className="mr-auto">{this.state.alertTrigger}</strong>
                    </Toast.Header>
                    <Toast.Body>{this.state.alertText}</Toast.Body>
                </Toast>
                {/*<Alert show={this.state.showAlert} variant={this.state.alertVariant} onClose={() => this.setState({ showAlert: !this.state.showAlert })} dismissible>*/}
                {/*    <p>*/}
                {/*        {this.state.alertText}*/}
                {/*    </p>*/}
                {/*</Alert>*/}
            </Container>
        );
    }
}
