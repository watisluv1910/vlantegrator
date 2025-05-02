import {createRoot} from 'react-dom/client'
import App from './App.jsx'
import {BrowserRouter} from "react-router-dom";
import {AuthProvider} from "react-oidc-context";
import {ProtectedApp} from "./components/ProtectedApp.jsx";
import {QueryClientProvider} from "@tanstack/react-query";
import {userManager} from "./config/keycloak";
import {onSignInCallback, queryClient} from "./config/config";
import {CssBaseline, ThemeProvider} from "@mui/material";
import {THEME} from "./styles/muiConfig";
import React from "react";
import {SidebarProvider} from "./hooks/useSidebarWidth.jsx";

createRoot(document.getElementById('root')).render(
    <BrowserRouter basename="/">
        <AuthProvider userManager={userManager} onSigninCallback={onSignInCallback}>
            <QueryClientProvider client={queryClient}>
                <ThemeProvider theme={THEME}>
                    <CssBaseline>
                        <ProtectedApp>
                            <SidebarProvider>
                                <App/>
                            </SidebarProvider>
                        </ProtectedApp>
                    </CssBaseline>
                </ThemeProvider>
            </QueryClientProvider>
        </AuthProvider>
    </BrowserRouter>
)
