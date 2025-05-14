import {AxiosError} from "axios";
import axiosRetry from "axios-retry";
import {client} from "../api/client.gen.ts";

import {
    AXIOS_RETRIES_COUNT,
    AXIOS_RETRIES_DELAY_MS,
    BASE_API_URL
} from "../utils/constants.js";
import {useAuth} from "react-oidc-context";

client.setConfig({
    baseURL: BASE_API_URL,
    withCredentials: true,
});

client.instance.interceptors.request.use(config => {
    const {user} = useAuth()
    console.log(user)
    if (user?.access_token) {
        // @ts-ignore
        config.headers = {
            ...config.headers,
            Authorization: `Bearer ${user.access_token}`,
        }
    }
    return config
});

axiosRetry(client.instance, {
    retries: AXIOS_RETRIES_COUNT,
    retryDelay: (retryCount) => {
        console.log(`retry attempt: ${retryCount}`);
        return retryCount * AXIOS_RETRIES_DELAY_MS;
    },
    retryCondition: (error: AxiosError) => {
        return axiosRetry.isNetworkError(error)
    },
});