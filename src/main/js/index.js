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
import {MethodsView} from "./components/MethodsView";

const ParameterizedFiltersView = (props) => {
    let { id } = useParams();

    return <FiltersView selectedId={id} showAlert={props.showAlert} />;
};

const ParameterizedMethodsView = (props) => {
    let { id, uuid } = useParams();

    return <MethodsView selectedPipelineId={id} selectedFilterUuid={uuid} showAlert={props.showAlert} />;
};

class App extends React.Component {

    render() {
        return (
            <HashRouter path={"/ui"}>
                <Switch>
                    <Route exact path={"/"}>
                        <Redirect from={"/"} to={"/pipelines"}/>
                    </Route>
                    <Route exact path={"/pipelines"}>
                        <Root>
                            <PipelinesView />
                        </Root>
                    </Route>
                    <Route exact path={"/filters"}>
                        <Root>
                            <FiltersView />
                        </Root>
                    </Route>
                    <Route exact path={"/pipelines/:id/filters"}>
                        <Root>
                            <ParameterizedFiltersView/>
                        </Root>
                    </Route>
                    <Route exact path={"/pipelines/:id/filters/:uuid/methods"}>
                        <Root>
                            <ParameterizedMethodsView/>
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
