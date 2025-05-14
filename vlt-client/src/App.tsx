import React from "react";
import {Route, Routes} from "react-router-dom";
import {MAIN_ROUTES} from "./utils/constants.js";
import {HomePage} from "./pages/Home.tsx";
import {Integrator} from "./pages/Integrator.js";

import {Info} from "./pages/Info.tsx";
import '@fontsource/ubuntu';
import '@fontsource/madimi-one';
import '@fontsource/ubuntu/500.css';
import '@fontsource/ubuntu/700.css';

export const App: React.FC = () => {
    return (
        <>
            <Routes>
                <Route path={MAIN_ROUTES.home.path}>
                    <Route index={true} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.integrator.path} element={<Integrator/>}/>
                    <Route path={MAIN_ROUTES.health.path} element={<Info/>}/>
                </Route>
            </Routes>
        </>
    );
}