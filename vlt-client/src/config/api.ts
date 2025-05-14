import axios, {AxiosError} from 'axios';
import axiosRetry from 'axios-retry';

import {
    AXIOS_RETRIES_COUNT,
    AXIOS_RETRIES_DELAY_MS,
    BASE_API_URL
} from '../utils/constants.js';

export const api = axios.create({
    baseURL: BASE_API_URL,
    withCredentials: true,
});

axiosRetry(api, {
    retries: AXIOS_RETRIES_COUNT,
    retryDelay: (retryCount) => {
        console.log(`retry attempt: ${retryCount}`);
        return retryCount * AXIOS_RETRIES_DELAY_MS;
    },
    retryCondition: (error: AxiosError) => {
        return axiosRetry.isNetworkError(error)
    },
});