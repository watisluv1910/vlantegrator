import {QueryClient} from "@tanstack/react-query";
import {AxiosError} from "axios";
import axiosRetry from "axios-retry";
import {client} from "../api/client.gen.ts";

import {
    AXIOS_RETRIES_COUNT,
    AXIOS_RETRIES_DELAY_MS,
    BASE_API_URL
} from "../utils/constants.js";

import {userManager} from "../config/keycloak.ts";

export const onSignInCallback = () => {
    window.history.replaceState({}, document.title, window.location.pathname);
};

// Transact Query Client

export const queryClient = new QueryClient();

// Axios Client

client.setConfig({
    baseURL: BASE_API_URL,
    withCredentials: true,
});

client.instance.interceptors.request.use(async (config) => {
    const user = await userManager.getUser();
    config.headers.Authorization = `Bearer ${user?.access_token}`;
    return config;
}, error => Promise.reject(error));

axiosRetry(client.instance, {
    retries: AXIOS_RETRIES_COUNT,
    retryDelay: (retryCount: number): number => {
        console.log(`retry attempt: ${retryCount}`);
        return retryCount * AXIOS_RETRIES_DELAY_MS;
    },
    retryCondition: (error: AxiosError) => {
        return axiosRetry.isNetworkError(error)
    },
});