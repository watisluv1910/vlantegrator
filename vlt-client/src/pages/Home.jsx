import React from 'react';
import {
    Box, Card, CardContent,
    FormControlLabel, Grid,
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

import {Header} from "../components/Header";
import {Sidebar} from "../components/Sidebar";
import {useSidebarWidth} from "../hooks/useSidebarWidth.jsx";

const healthMetrics = [
    {label: 'Использование ЦПУ', value: '8%'},
    {label: 'Память', value: '5.0 GB / 12 GB'},
    {label: 'Статус БД Платформы', value: 'Healthy'},
    {label: 'Статус Kafka', value: 'Healthy'},
];

const recentActivity = [
    {
        user: 'Test Admin',
        time: '21 мин назад',
        action: 'развернул маршрут "Интеграция с БД теста" (ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6.59df977)',
        icon: SendIcon,
        color: "success"
    },
    {
        user: 'Test Admin',
        time: '46 мин назад',
        action: 'остановил маршрут "Интеграция с БД теста" (ID: 3fa85f64-5717-4562-b3fc-2c963f66afa6.59df977)',
        icon: StopCircleIcon,
        color: "success"
    }
];

export const HomePage = () => {
    const [showCurrUserActivity, setShowCurrUserActivity] = React.useState(false);
    const [sidebarWidth, _] = useSidebarWidth();
    return (
        <>
            <Header path={["Главная"]}/>
            <Sidebar/>

            <Box
                component="main"
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

                <Grid item sx={{xs: 12, md: 6, mt: 2}}>
                    <Card>
                        <CardContent>
                            <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                                <Typography variant="h6">Активности</Typography>
                                <FormControlLabel
                                    control={<Switch checked={showCurrUserActivity}
                                                     onChange={e => setShowCurrUserActivity(e.target.checked)}/>}
                                    label={showCurrUserActivity ? 'Моя активность' : 'Общая активность'}
                                />
                            </Box>
                            <Timeline position="alternate">
                                {recentActivity.map((a, idx) => {
                                    const DotIcon = a.icon;
                                    return <TimelineItem key={idx}>
                                        <TimelineOppositeContent color="primary.main"
                                                                 sx={{mt: 1.35, width: '100%'}}>
                                            {a.time}
                                        </TimelineOppositeContent>
                                        <TimelineSeparator>
                                            <TimelineDot color={a.color}>
                                                <DotIcon fontSize="medium"/>
                                            </TimelineDot>
                                            {idx < recentActivity.length - 1 && <TimelineConnector/>}
                                        </TimelineSeparator>
                                        <TimelineContent>
                                            <Typography>{`${a.user} ${a.action}`}</Typography>
                                        </TimelineContent>
                                    </TimelineItem>
                                })}
                            </Timeline>
                        </CardContent>
                    </Card>
                </Grid>
            </Box>
        </>
    );
}