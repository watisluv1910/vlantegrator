import React from 'react';
import {useQuery} from '@tanstack/react-query';
import {api} from "../config/api.js";
import {useAuth} from "react-oidc-context";

export const Info = () => {
    let auth = useAuth();

    const {data, isLoading, isError, error} = useQuery({
        queryKey: ['tokenInfo'],
        queryFn: async () => {
            const response = await api.get('/token-info', {
                headers: {
                    Authorization: `Bearer ${auth.user?.access_token}`
                }
            });
            return response.data;
        },
    });

    if (isLoading) {
        return <div>Загрузка информации о токене...</div>;
    }

    if (isError) {
        return <div>Ошибка: {error.message}</div>;
    }

    return (
        <div>
            <h1>Информация о токене</h1>
            <p>{JSON.stringify(data, null, 2)}</p>
        </div>
    );
};