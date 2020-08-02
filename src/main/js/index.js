import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import '../resources/static/main.css'

import React from "react";
import {
    HashRouter,
    Route,
    Switch,
    Redirect,
    useParams
} from 'react-router-dom';
import {render} from 'react-dom';
import {PipelinesView} from "./components/PipelinesView";
import {Root} from "./components/Root";
import {FiltersView} from "./components/FiltersView";

const ParameterizedFiltersView =() => {
    let { id } = useParams();

    return <FiltersView selectedId={id} />;
};

class App extends React.Component {

    render() {
        return (
            <HashRouter path={"/ui"}>
                <Switch>
                    <Route exact path={"/"}>
                        <Redirect from={"/"} to={"/pipelines"}/>
                    </Route>
                    <Route path={"/pipelines"}>
                        <Root>
                            <PipelinesView />
                        </Root>
                    </Route>
                    <Route exact path={"/filters"}>
                        <Root>
                            <FiltersView />
                        </Root>
                    </Route>
                    <Route exact path={"/filters/:id"}>
                        <Root>
                            <ParameterizedFiltersView/>
                        </Root>
                    </Route>
                </Switch>
            </HashRouter>
        );
    }
}

render(
    <App />,
    document.getElementById('react')
);
