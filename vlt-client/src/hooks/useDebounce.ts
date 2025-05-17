import {useEffect, useState} from "react";

export function useDebounce<T>(value: T, delayMs: number): T {
    const [debounced, setDebounced] = useState(value);
    useEffect(() => {
        const handler = setTimeout(() => setDebounced(value), delayMs);
        return () => clearTimeout(handler);
    }, [value, delayMs]);
    return debounced;
}