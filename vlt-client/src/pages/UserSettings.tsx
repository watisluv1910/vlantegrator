import React, {useEffect, useState} from "react";
import {
    Box,
    Typography,
    FormControl,
    FormControlLabel,
    Switch,
    Select,
    MenuItem,
    InputLabel,
    SelectChangeEvent,
    CircularProgress,
    Alert,
    Paper,
    Divider,
} from "@mui/material";
import {useQuery, useMutation, useQueryClient} from "@tanstack/react-query";
import {
    UserApiService,
    UserSettingsDto,
    UserEditorSettingsDto,
    UserAccessibilitySettingsDto,
} from "@/api";
import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";

const AUTOSAVE_OPTIONS = [
    {label: "Отключено", value: -1},
    {label: "Каждые 10 секунд", value: 10_000},
    {label: "Каждые 30 секунд", value: 30_000},
    {label: "Каждую минуту", value: 60_000},
    {label: "Каждые 5 минут", value: 300_000},
];

const VIEWPORT_POSITION_OPTIONS = [
    {label: "Начало (0:0)", value: "origin"},
    {label: "Центр схемы", value: "center"},
    {label: "Внизу схемы", value: "bottom"},
];

export const UserSettingsPage: React.FC = () => {
    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();
    const queryClient = useQueryClient();

    const {data: settings, isLoading, isError} = useQuery<UserSettingsDto, Error>({
        queryKey: ["userSettings"],
        queryFn: () => UserApiService.getUserSettings().then(r => r.data),
        staleTime: 60_000
    });

    const [local, setLocal] = useState<UserSettingsDto | null>(null);

    useEffect(() => {
        if (settings) setLocal(settings);
    }, [settings]);

    const updateMutation = useMutation({
        mutationFn: (newSettings: UserSettingsDto) =>
            UserApiService.updateUserSettings({body: newSettings}),
        onSuccess: () => {
            if (local) queryClient.setQueryData(["userSettings"], local);
        },
    });

    const handleEditorChange =
        <K extends keyof UserEditorSettingsDto>(key: K) =>
            (e: SelectChangeEvent | React.ChangeEvent<HTMLInputElement>) => {
                if (!local) return;
                const raw = e.target.value;
                const parsed =
                    key === "showGrid"
                        ? (e.target as HTMLInputElement).checked
                        : key === "autosaveIntervalMs" ? Number(raw) : raw;
                const updated: UserSettingsDto = {
                    ...local,
                    editor: {...local.editor, [key]: parsed},
                };
                setLocal(updated);
                updateMutation.mutate(updated);
            };

    const handleAccessibilityChange =
        <K extends keyof UserAccessibilitySettingsDto>(key: K) =>
            (e: React.ChangeEvent<HTMLInputElement>) => {
                if (!local) return;
                const updated: UserSettingsDto = {
                    ...local,
                    accessibility: {...local.accessibility, [key]: e.target.checked},
                };
                setLocal(updated);
                updateMutation.mutate(updated);
            };

    if (isLoading || !local) {
        return (
            <>
                <Header currPath={["Настройки"]}/>
                <Sidebar/>

                <Box textAlign="center" sx={{
                    ml: `${sidebarWidth}px`,
                    transition: "margin .2s",
                    display: "flex",
                    justifyContent: "center",
                    p: 3,
                }}>
                    <CircularProgress/>
                </Box>
            </>
        );
    }

    if (isError) {
        return (
            <>
                <Header currPath={["Настройки"]}/>
                <Sidebar/>

                <Box sx={{
                    ml: `${sidebarWidth}px`,
                    transition: "margin .2s",
                    display: "flex",
                    justifyContent: "center",
                    p: 3,
                }}>
                    <Alert severity="error">Не удалось загрузить настройки пользователя.</Alert>
                </Box>
            </>
        );
    }

    return (
        <>
            <Header currPath={["Настройки"]}/>
            <Sidebar/>

            <Box
                sx={{
                    ml: `${sidebarWidth}px`,
                    transition: "margin .2s",
                    display: "flex",
                    justifyContent: "center",
                    p: 3,
                }}
            >
                <Box sx={{width: "100%", maxWidth: 600}}>
                    <Typography variant="h4" align="center" gutterBottom>
                        Пользовательские предпочтения
                    </Typography>

                    <Divider sx={{my: 2}}/>

                    <Paper sx={{p: 3, mb: 4}} elevation={2}>
                        <Typography variant="h6" align="center" gutterBottom>
                            Параметры редактора
                        </Typography>

                        <FormControlLabel
                            sx={{mt: 2}}
                            control={
                                <Switch
                                    checked={local.editor.showGrid}
                                    onChange={handleEditorChange("showGrid")}
                                />
                            }
                            label="Показывать сетку"
                        />

                        <FormControl fullWidth sx={{mt: 3}}>
                            <InputLabel id="position-select-label">
                                Позиция по-умолчанию
                            </InputLabel>
                            <Select
                                labelId="position-select-label"
                                label="Позиция по-умолчанию"
                                value={local.editor.defaultViewportPosition}
                                onChange={handleEditorChange("defaultViewportPosition")}
                            >
                                {VIEWPORT_POSITION_OPTIONS.map(opt => (
                                    <MenuItem key={opt.value} value={opt.value}>{opt.label}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl fullWidth sx={{mt: 3}}>
                            <InputLabel id="autosave-select-label">Автосохранение</InputLabel>
                            <Select
                                labelId="autosave-select-label"
                                label="Автосохранение"
                                value={String(local.editor.autosaveIntervalMs)}
                                onChange={handleEditorChange("autosaveIntervalMs")}
                            >
                                {AUTOSAVE_OPTIONS.map(opt => (
                                    <MenuItem key={opt.value} value={String(opt.value)}>
                                        {opt.label}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Paper>

                    <Paper sx={{display: "flex", flexDirection: "column", p: 3}}>
                        <Typography variant="h6" align="center" gutterBottom>
                            Доступность
                        </Typography>

                        <FormControlLabel
                            sx={{mt: 1}}
                            control={
                                <Switch
                                    checked={local.accessibility.disableAnimations}
                                    onChange={handleAccessibilityChange("disableAnimations")}
                                />
                            }
                            label="Отключить анимации"
                        />

                        <FormControlLabel
                            sx={{mt: 1}}
                            control={
                                <Switch
                                    checked={local.accessibility.enableHighContrast}
                                    onChange={handleAccessibilityChange("enableHighContrast")}
                                />
                            }
                            label="Режим высокой контрастности"
                        />
                    </Paper>
                </Box>
            </Box>
        </>
    );
};