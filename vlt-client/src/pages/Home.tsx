import React from 'react';
import {
    Card, CardContent,
    FormControlLabel, Grid, Stack, SvgIconTypeMap,
    Switch,
    Typography
} from "@mui/material";
import {
    StopCircle as StopCircleIcon,
    Send as SendIcon,
} from '@mui/icons-material';
import {
    Timeline,
    TimelineConnector,
    TimelineContent,
    TimelineDot,
    TimelineItem,
    TimelineOppositeContent,
    TimelineSeparator
} from "@mui/lab";

import {Header} from "../components/Header.js";
import {Sidebar} from "../components/Sidebar.js";
import {useSidebarWidth} from "../hooks/useSidebarState.tsx";
import {OverridableComponent} from "@mui/material/OverridableComponent";

export type HealthMetric = {
    label: string;
    value: string;
};

export type ActivityEntry = {
    user: string;
    time: string;
    action: string;
    icon: OverridableComponent<SvgIconTypeMap>;
};

const healthMetrics: HealthMetric[] = [
    {label: 'Использование ЦПУ', value: '8%'},
    {label: 'Память', value: '5.0 GB / 12 GB'},
    {label: 'Статус БД Платформы', value: 'Healthy'},
    {label: 'Статус Kafka', value: 'Healthy'},
];

const recentActivity: ActivityEntry[] = [
    {
        user: 'Test Admin',
        time: '21 мин назад',
        action: 'развернул маршрут "Интеграция с БД теста" (ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6.59df977)',
        icon: SendIcon,
    },
    {
        user: 'Test Admin',
        time: '46 мин назад',
        action: 'остановил маршрут "Интеграция с БД теста" (ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6.59df977)',
        icon: StopCircleIcon,
    }
];

export const HomePage = () => {
    const [showCurrUserActivity, setShowCurrUserActivity] = React.useState(false);
    const [sidebarWidth] = useSidebarWidth();
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
                    transition: 'margin .2s'
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
                        <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2} paddingInline={2}>
                            <Typography variant="h4" color="accent">Активности</Typography>
                            <FormControlLabel
                                control={<Switch checked={showCurrUserActivity}
                                                 onChange={e => setShowCurrUserActivity(e.target.checked)}/>}
                                label={showCurrUserActivity ? 'Моя активность' : 'Общая активность'}
                            />
                        </Stack>
                        <Timeline position="alternate">
                            {recentActivity.map((activity, idx) => {
                                const DotIcon = activity.icon;
                                return <TimelineItem key={idx}>
                                    <TimelineOppositeContent color="primary.main" sx={{mt: 1.35, width: '100%'}}>
                                        {activity.time}
                                    </TimelineOppositeContent>
                                    <TimelineSeparator>
                                        <TimelineDot color="success">
                                            <DotIcon fontSize="medium"/>
                                        </TimelineDot>
                                        {idx < recentActivity.length - 1 && <TimelineConnector/>}
                                    </TimelineSeparator>
                                    <TimelineContent>
                                        <Typography>{`${activity.user} ${activity.action}`}</Typography>
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