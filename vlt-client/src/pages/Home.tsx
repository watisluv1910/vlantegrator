import React from "react";
import {
    Button,
    Card,
    CardContent, Divider,
    Grid,
    Stack,
    SvgIconTypeMap,
    Typography,
    useTheme
} from "@mui/material";
import {
    Timeline,
    TimelineConnector,
    TimelineContent,
    TimelineDot,
    TimelineItem,
    TimelineOppositeContent,
    TimelineSeparator
} from "@mui/lab";
import {OverridableComponent} from "@mui/material/OverridableComponent";
import {Header} from "../components/Header.js";
import {Sidebar} from "../components/Sidebar.js";
import {HealthApiService, RoutesApiService} from "../api/sdk.gen.ts";
import {usePolling} from "../hooks/usePolling.ts";
import {formatBytes, iconForAction, relativeTimeFromDates} from "../utils/transformers.ts";
import {MainSidebarContext} from "../hooks/sidebarContexts.tsx";
import {BASIC_PLATFORM_HEALTH_POLLING_INTERVAL_MS, RECENT_ACTIVITY_POLLING_INTERVAL_MS} from "../utils/constants.tsx";

export type HealthMetric = {
    label: string;
    value: string;
};

export type ActivityEntry = {
    routeId: string;
    user: string;
    time: string;
    action: string;
    icon: OverridableComponent<SvgIconTypeMap>;
};

export const HomePage = () => {
    const [showCurrUserActivity, setShowCurrUserActivity] = React.useState(false);
    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();
    const theme = useTheme();

    const {data: activities} = usePolling(
        ["recentActivity", showCurrUserActivity],
        () => RoutesApiService.getRouteUserActions({
            query: {
                personal: showCurrUserActivity,
                limit: 50,
            }
        }),
        RECENT_ACTIVITY_POLLING_INTERVAL_MS,
    );

    const recentActivities: ActivityEntry[] | undefined = activities?.data?.map(action => ({
        routeId: `${action.routeId.id}`,
        user: action.userDisplayName,
        time: relativeTimeFromDates(new Date(parseInt(action.attemptedAt) * 1000), new Date()),
        action: action.action,
        icon: iconForAction(action.action),
    }));

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
            <Header currPath={["Главная"]}/>
            <Sidebar/>
            <Stack
                component="main"
                direction="column"
                spacing={3}
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

                <Card>
                    <CardContent>
                        <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}
                               paddingInline={2}>
                            <Typography variant="h4" color="accent">Активности</Typography>
                            <Button variant="outlined"
                                    onClick={(_) => setShowCurrUserActivity(!showCurrUserActivity)}
                            >
                                {showCurrUserActivity ? "Моя активность" : "Общая активность"}
                            </Button>
                        </Stack>

                        <Divider variant="middle" aria-hidden={true} flexItem sx={{mt: 2}}/>

                        <Timeline position="alternate">
                            {recentActivities?.map((activity, idx) => {
                                const DotIcon = activity.icon;
                                return <TimelineItem key={idx}>
                                    <TimelineOppositeContent color="primary.main" sx={{mt: 1.35, width: "100%"}}>
                                        {activity.time}
                                    </TimelineOppositeContent>
                                    <TimelineSeparator>
                                        <TimelineDot sx={{bgcolor: theme.palette.success.dark}}>
                                            <DotIcon fontSize="medium"/>
                                        </TimelineDot>
                                        {idx < recentActivities.length - 1 && <TimelineConnector/>}
                                    </TimelineSeparator>
                                    <TimelineContent>
                                        <Typography>
                                            {`Пользователь ${activity.user} совершил 
                                            действие ${activity.action.toUpperCase()} 
                                            над маршрутом с ID: ${activity.routeId}`}
                                        </Typography>
                                    </TimelineContent>
                                </TimelineItem>
                            })}
                        </Timeline>
                    </CardContent>
                </Card>
            </Stack>
        </>
    );
}