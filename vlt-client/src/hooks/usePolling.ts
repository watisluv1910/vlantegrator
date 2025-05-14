import {useQuery, QueryKey, UseQueryResult} from "@tanstack/react-query";

export function usePolling<T>(
    queryKey: QueryKey,
    queryFn: () => Promise<T>,
    intervalMs: number
): UseQueryResult<T, Error> {
    return useQuery({
        queryKey,
        queryFn,
        refetchInterval: intervalMs,
        refetchIntervalInBackground: true,
    });
}