import {App} from "./App.tsx"
import {createRoot} from "react-dom/client"
import {BrowserRouter} from "react-router-dom";
import {AuthProvider} from "react-oidc-context";
import {ProtectedApp} from "./components/ProtectedApp.js";
import {QueryClientProvider} from "@tanstack/react-query";
import {userManager} from "./config/keycloak.ts";
import {onSignInCallback, queryClient} from "./config/config.ts";
import {CssBaseline, ThemeProvider} from "@mui/material";
import {THEME} from "./styles/muiConfig.js";
import {SidebarProvider} from "./hooks/useSidebarState.tsx";

const container = document.getElementById("root");

if (!container) {
    throw new Error("#root element not found");
}

const root = createRoot(container);

root.render(
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
    </BrowserRouter>,
);
