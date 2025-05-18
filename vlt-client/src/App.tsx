import React from "react";
import {Route, Routes} from "react-router-dom";
import {MAIN_ROUTES} from "@/utils/constants.js";
import {HomePage} from "@/pages/Home.tsx";
import {RoutesPage} from "@/pages/route/Routes.tsx";

import "@fontsource/ubuntu";
import "@fontsource/madimi-one";
import "@fontsource/ubuntu/500.css";
import "@fontsource/ubuntu/700.css";
import {CreateRoutePage} from "@/pages/route/CreateRoute.tsx";
import {IntegratorPage} from "@/pages/Integrator.tsx";

export const App: React.FC = () => {
    return (
        <>
            <Routes>
                <Route path={MAIN_ROUTES.home.path}>
                    <Route index={true} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.observer.path} element={<RoutesPage/>}/>
                    <Route path="/routes/create" element={<CreateRoutePage />} />
                    {/*<Route path="/routes/:id/edit" element={<EditRoutePage />} />*/}
                    <Route path="/routes/:id/definition/:versionHash" element={<IntegratorPage />} />
                    <Route path={MAIN_ROUTES.health.path} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.docs.path} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.help.path} element={<HomePage/>}/>
                    <Route path={MAIN_ROUTES.settings.path} element={<HomePage/>}/>
                </Route>
            </Routes>
        </>
    );
}