import {QueryClient} from "@tanstack/react-query";

export const onSignInCallback = () => {
    window.history.replaceState({}, document.title, window.location.pathname);
};

export const queryClient = new QueryClient();