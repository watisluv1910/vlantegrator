import {UserManager, WebStorageStateStore} from "oidc-client-ts";

export const ACCOUNT_MANAGEMENT_URL: string = `${import.meta.env.VITE_AUTHORITY}/account`;

export const userManager: UserManager = new UserManager({
    authority: import.meta.env.VITE_AUTHORITY,
    client_id: import.meta.env.VITE_CLIENT_ID,
    redirect_uri: `${window.location.origin}${window.location.pathname}`,
    post_logout_redirect_uri: window.location.origin,
    userStore: new WebStorageStateStore({
        store: window.sessionStorage,
        prefix: "authorization",
    }),
    monitorSession: true, // cross-tab login/logout
});