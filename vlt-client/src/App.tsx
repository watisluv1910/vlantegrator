import React from "react";
import {Route, Routes} from "react-router-dom";
import {MAIN_ROUTES} from "@/utils/constants.js";
import {HomePage} from "@/pages/Home.tsx";
import {RoutesPage} from "@/pages/route/Routes.tsx";

import "@fontsource/ubuntu";
import "@fontsource/madimi-one";
import "@fontsource/ubuntu/500.css";
import "@fontsource/ubuntu/700.css";

export const App: React.FC = () => {
    return (
        <>
            <Routes>
                <Route path={MAIN_ROUTES.home.path}>
                    <Route index={true} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.observer.path} element={<RoutesPage/>}/>
                    <Route path={MAIN_ROUTES.health.path} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.docs.path} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.help.path} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.settings.path} element={<HomePage/>}/>
                </Route>
            </Routes>
        </>
    );
}