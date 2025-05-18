import React, {useState, ChangeEvent, FormEvent, useEffect} from "react";
import {
    Box,
    Button,
    TextField,
    MenuItem,
    Chip,
    Select,
    InputLabel,
    FormControl,
    OutlinedInput,
    Stack,
    Divider,
    Typography,
    ListItemIcon, SelectChangeEvent,
} from "@mui/material";
import {
    Add as AddIcon,
    Cancel as CancelIcon
} from "@mui/icons-material";
import type {
    CreateRouteRequestDto, DockerNetworkDto,
    UpdateRouteRequestDto,
} from "@/api/types.gen.ts";

interface RouteFormProps {
    networks: DockerNetworkDto[];
    onCreateNetworkClick: () => void;
    initialValues: Partial<CreateRouteRequestDto & UpdateRouteRequestDto & {
        id?: string;
        networks?: DockerNetworkDto[]
    }>;
    showIdField?: boolean;
    submitLabel: string;
    onSubmit: (values: CreateRouteRequestDto | UpdateRouteRequestDto) => Promise<void>;
}

const CREATE_NEW = "__create_new__";

export const RouteForm: React.FC<RouteFormProps> = (props: RouteFormProps) => {
    const showIdField = props.showIdField === undefined ? false : props.showIdField;
    const initialNetworkNames = props.initialValues.networks?.map((n) => n.name) || [];

    const [form, setForm] = useState({
        id: props.initialValues.id || "",
        name: props.initialValues.name || "",
        description: props.initialValues.description,
        ownerName: props.initialValues.ownerName,
        publishedPorts: props.initialValues.publishedPorts,
        networks: initialNetworkNames,
        env: props.initialValues.env || {},
    });

    const [envText, setEnvText] = useState(JSON.stringify(form.env, null, 2));

    useEffect(() => {
        setForm((f) => ({
            ...f,
            id: props.initialValues.id || "",
            name: props.initialValues.name || "",
            description: props.initialValues.description,
            ownerName: props.initialValues.ownerName,
            publishedPorts: props.initialValues.publishedPorts,
            networks: initialNetworkNames,
            env: props.initialValues.env || {},
        }));
        setEnvText(JSON.stringify(props.initialValues.env || {}, null, 2));
    }, [props.initialValues]);

    const handleChange =
        (field: keyof typeof form) =>
            (e: ChangeEvent<HTMLInputElement | { value: unknown }>) => {
                const value = e.target.value as any;
                setForm((f) => ({...f, [field]: value}));
            };

    const handleNetworksChange = (e: SelectChangeEvent<string[]>) => {
        const value = e.target.value as string[];
        if (value.includes(CREATE_NEW)) {
            props.onCreateNetworkClick();
            return;
        }
        setForm((f) => ({...f, networks: value}));
    };

    const handleRemoveNetwork = (nameToRemove: string) => {
        setForm((f) => ({
            ...f,
            networks: f.networks.filter((n) => n !== nameToRemove),
        }));
    };

    const handleEnvBlur = () => {
        try {
            const parsed = JSON.parse(envText);
            setForm((f) => ({...f, env: parsed}));
        } catch {
            // TODO: нотификация о невалидном JSON
        }
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        const selectedNetworks: DockerNetworkDto[] = props.networks.filter(net => form.networks.includes(net.name));
        const dto = {
            ...(showIdField ? { id: form.id } : {}),
            name: form.name,
            description: form.description,
            ownerName: form.ownerName,
            publishedPorts: form.publishedPorts,
            networks: selectedNetworks,
            env: form.env
        } as CreateRouteRequestDto | UpdateRouteRequestDto;
        await props.onSubmit(dto);
    };

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{maxWidth: 600, mx: "auto", mt: 4}}>
            <Stack spacing={2}>
                {showIdField && (
                    <TextField label="Идентификатор маршрута" value={form.id} disabled fullWidth/>
                )}

                <TextField
                    label="Название"
                    value={form.name}
                    onChange={handleChange("name")}
                    required
                    fullWidth
                />
                <TextField
                    label="Описание"
                    value={form.description}
                    onChange={handleChange("description")}
                    fullWidth
                />
                <TextField
                    label="Имя владельца (пользователя)"
                    value={form.ownerName}
                    onChange={handleChange("ownerName")}
                    helperText="Если пустое, владельцем будет считаться создатель маршрута"
                    fullWidth
                />
                <TextField
                    label="Маппинг портов"
                    value={form.publishedPorts}
                    onChange={handleChange("publishedPorts")}
                    fullWidth
                    placeholder={"9123:8080,5433:5432"}
                />

                <FormControl fullWidth>
                    <InputLabel>Сети</InputLabel>
                    <Select
                        multiple
                        value={form.networks}
                        onChange={handleNetworksChange}
                        input={<OutlinedInput label="Сети"/>}
                        renderValue={(selected) => (
                            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                                {(selected as string[]).map((name) => (
                                    <Chip
                                        key={name}
                                        label={name}
                                        size="small"
                                        onMouseDown={(e) => e.stopPropagation()}
                                        onDelete={() => handleRemoveNetwork(name)}
                                        deleteIcon={<CancelIcon fontSize="small" />}
                                    />
                                ))}
                            </Box>
                        )}
                        MenuProps={{PaperProps: {sx: {maxHeight: 240}}}}
                    >
                        {props.networks.map((net) => (
                            <MenuItem key={net.name} value={net.name}>
                                <ListItemIcon>
                                    <AddIcon fontSize="small"/>
                                </ListItemIcon>
                                {net.name} ({net.driver})
                            </MenuItem>
                        ))}

                        <Divider sx={{my: 1}}/>

                        <MenuItem value={CREATE_NEW} disableRipple>
                            <ListItemIcon>
                                <AddIcon fontSize="small"/>
                            </ListItemIcon>
                            <Typography variant="body2">Создать новую сеть…</Typography>
                        </MenuItem>
                    </Select>
                </FormControl>

                <TextField
                    label="Переменные среды (JSON)"
                    value={envText}
                    onChange={(e) => setEnvText(e.target.value)}
                    onBlur={handleEnvBlur}
                    multiline
                    minRows={4}
                    fullWidth
                />

                <Box sx={{textAlign: "right", mt: 2}}>
                    <Button type="submit" variant="contained">
                        {props.submitLabel}
                    </Button>
                </Box>
            </Stack>
        </Box>
    );
};