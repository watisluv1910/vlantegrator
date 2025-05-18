import {
    Box,
    Card,
    CardContent,
    Grid,
    Typography,
} from "@mui/material";
import {Header} from "@/components/Header.js";
import {Sidebar} from "@/components/Sidebar.js";
import {HealthApiService} from "@/api/sdk.gen.ts";
import {usePolling} from "@/hooks/usePolling.ts";
import {formatBytes} from "@/utils/transformers.ts";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {BASIC_PLATFORM_HEALTH_POLLING_INTERVAL_MS} from "@/utils/constants.tsx";

export type HealthMetric = {
    label: string;
    value: string;
};

export const PlatformStatePage = () => {
    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();

    const {data: health, isLoading: isHealthLoading, isError: isHealthError} = usePolling(
        ["basicHealth"],
        () => HealthApiService.getBasicHealth(),
        BASIC_PLATFORM_HEALTH_POLLING_INTERVAL_MS,
    );

    const healthMetrics: HealthMetric[] = [
        {
            label: "Использование ЦПУ",
            value: health
                ? `${health.data.cpuPercent.toFixed(1)}%`
                : isHealthLoading ? "…" : isHealthError ? "Error" : "-",
        },
        {
            label: "Память",
            value: health
                ? `${formatBytes(health.data.memUsedBytes)} / ${formatBytes(health.data.memTotalBytes)}`
                : isHealthLoading ? "…" : isHealthError ? "Error" : "-",
        },
        {
            label: "Статус БД Платформы",
            value: health?.data.dbStatus ?? (isHealthLoading ? "…" : isHealthError ? "Error" : "-"),
        },
        {
            label: "Статус Kafka",
            value: health?.data.kafkaStatus ?? (isHealthLoading ? "…" : isHealthError ? "Error" : "-"),
        },
    ];

    return (
        <>
            <Header currPath={["Состояние системы"]}/>
            <Sidebar/>
            <Box
                sx={{
                    flexGrow: 1,
                    p: 3,
                    ml: `${sidebarWidth}px`,
                    transition: "margin .2s"
                }}
            >
                <Grid container spacing={2}>
                    {healthMetrics.map(m => (
                        <Grid size={{xs: 12, sm: 6, md: 3}} key={m.label}>
                            <Card>
                                <CardContent>
                                    <Typography variant="subtitle2">{m.label}</Typography>
                                    <Typography variant="h5">{m.value}</Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            </Box>
        </>
    );
}