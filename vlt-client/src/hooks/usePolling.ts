import {useQuery, QueryKey, UseQueryResult, keepPreviousData} from "@tanstack/react-query";

export function usePolling<T>(
    queryKey: QueryKey,
    queryFn: () => Promise<T>,
    intervalMs: number,
): UseQueryResult<T, Error> {
    return useQuery({
        queryKey,
        queryFn,
        refetchInterval: intervalMs,
        staleTime: intervalMs,
        refetchOnWindowFocus: false,
        refetchIntervalInBackground: true,
        placeholderData: keepPreviousData,
    });
}