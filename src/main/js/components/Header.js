import React from 'react';
import {Nav, Navbar} from "react-bootstrap";
import {useLocation} from 'react-router-dom';

export const Header = (props) => {
    let location = useLocation();

    return (
        <Navbar bg={"primary"} variant={"dark"}>
            <Navbar.Brand href={"/ui"}>Pipelines UI</Navbar.Brand>
            <Nav className={"mr-auto"}>
                <Nav.Link href={"/ui#/pipelines"} className={location.pathname === '/pipelines' ? "active" : ""}>Pipelines</Nav.Link>
                <Nav.Link href={"/ui#/filters"} className={location.pathname === '/filters' ? "active" : ""}>Filters</Nav.Link>
            </Nav>
        </Navbar>
    );
};
