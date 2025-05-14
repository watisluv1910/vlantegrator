import {Header} from "../components/Header.tsx";
import {Sidebar} from "../components/Sidebar.tsx";
import {useCallback, useState} from "react";
import {useSidebarWidth} from "../hooks/useSidebarState.tsx";
import {
    addEdge,
    Background,
    Controls,
    MarkerType,
    Position,
    ReactFlow,
    useEdgesState,
    useNodesState
} from "@xyflow/react";
import {
    Storage as JdbcAdapterIcon,
    Http as HttpAdapterIcon,
    Transform as TransformerAdapterIcon,
    HistoryEdu as LoggerAdapterIcon,
    Save as SaveIcon,
    Verified as ValidateIcon,
    Handyman as BuildIcon,
    Start as StartIcon,
    StopCircle as StopIcon,
    RestartAlt as RestartIcon,
    DeleteForever as DeleteIcon,
    Bolt as BoltIcon,
} from "@mui/icons-material";
import {
    Box,
    Checkbox, Divider,
    FormControl, IconButton,
    ListItemText,
    MenuItem,
    OutlinedInput,
    Paper,
    Select,
    TextField,
    Typography
} from "@mui/material";

import '@xyflow/react/dist/style.css';
import {useColorScheme} from "@mui/material/styles";

const nodeDefaults = {
    sourcePosition: Position.Bottom,
    targetPosition: Position.Top,
};

const initialNodes = [
    {
        id: 'http-inbound',
        type: 'default',
        data: {
            label: (
                <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1}}>
                    <HttpAdapterIcon fontSize="large"/>
                    <Typography>HTTP Inbound Gateway</Typography>
                </Box>
            )
        },
        position: {x: 250, y: 0},
        style: {width: 180, height: 100, borderRadius: 16},
        targetPosition: Position.Bottom,
    },
    {
        id: 'transform',
        type: 'default',
        data: {
            label: (
                <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1}}>
                    <TransformerAdapterIcon fontSize="large"/>
                    <Typography>Transformer</Typography>
                </Box>
            )
        },
        position: {x: 250, y: 140},
        style: {width: 140, height: 80, borderRadius: 16},
        ...nodeDefaults,
    },
    {
        id: 'log-http',
        type: 'default',
        data: {
            label: (
                <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1}}>
                    <LoggerAdapterIcon fontSize="large"/>
                    <Typography>Logger</Typography>
                </Box>
            )
        },
        position: {x: 100, y: 140},
        style: {width: 100, height: 80, borderRadius: 16},
        ...nodeDefaults,
    },
    {
        id: 'log-transform',
        type: 'default',
        data: {
            label: (
                <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1}}>
                    <LoggerAdapterIcon fontSize="large"/>
                    <Typography>Logger</Typography>
                </Box>
            )
        },
        position: {x: 400, y: 260},
        style: {width: 100, height: 80, borderRadius: 16},
        ...nodeDefaults,
    },
    {
        id: 'jdbc-outbound',
        type: 'default',
        data: {
            label: (
                <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1}}>
                    <JdbcAdapterIcon fontSize="large"/>
                    <Typography>JDBC Outbound Gateway</Typography>
                </Box>
            )
        },
        position: {x: 250, y: 280},
        style: {width: 200, height: 100, borderRadius: 16},
        ...nodeDefaults,
    }
];

const initialEdges = [
    {
        id: 'e1',
        source: 'http-inbound',
        target: 'transform',
        type: 'default',
        markerEnd: {type: MarkerType.ArrowClosed},
        animated: true
    },
    {id: 'e2', source: 'http-inbound', target: 'log-http', type: 'default', markerEnd: {type: MarkerType.ArrowClosed}},
    {
        id: 'e3',
        source: 'transform',
        target: 'jdbc-outbound',
        type: 'default',
        markerEnd: {type: MarkerType.ArrowClosed},
        animated: true
    },
    {id: 'e4', source: 'transform', target: 'log-transform', type: 'default', markerEnd: {type: MarkerType.ArrowClosed}}
];

export const Integrator = () => {
    const [sidebarWidth, _] = useSidebarWidth();

    const [nodes, _setNodes, onNodesChange] = useNodesState(initialNodes);
    const [edges, _setEdges, onEdgesChange] = useEdgesState(initialEdges);

    const [menuOpen, setMenuOpen] = useState(false);

    const onConnect = useCallback(
        (params: any) => { // TODO
            onEdgesChange(addEdge(params, edges));
        },
        [edges, onEdgesChange]
    );

    const {mode} = useColorScheme();
    const [colorMode] = useState(mode);

    const [adapterConfig, setAdapterConfig] = useState({
        path: "/data",
        requestPayloadType: "java.lang.String",
        supportedMethods: ["POST"]
    });

    return (
        <Box>
            <Header
                currPath={["Интегратор", "Маршруты", "Интеграция с БД теста", "Настройка потока"]}/>
            <Sidebar/>
            <Box style={{height: '92vh', width: `calc(100%-${sidebarWidth}px)`}}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    attributionPosition={"bottom-left"}
                    nodesDraggable={true}
                    nodesConnectable={true}
                    elementsSelectable={true}
                    colorMode={colorMode}
                    style={{width: '100%', height: '100%'}}
                >
                    <Controls position={"bottom-right"}/>
                    <Background/>
                </ReactFlow>
            </Box>
            <Paper elevation={3} sx={{
                position: 'fixed',
                top: 60,
                right: 0,
                width: 300,
                height: '100vh',
                p: 2,
                overflow: 'auto'
            }}>
                <Typography variant="h6" gutterBottom>
                    HTTP Inbound Config
                </Typography>
                <Typography variant="subtitle2">Path</Typography>
                <TextField
                    fullWidth
                    size="small"
                    value={adapterConfig.path}
                    onChange={(e) => setAdapterConfig({...adapterConfig, path: e.target.value})}
                />
                <Typography variant="subtitle2" sx={{mt: 2}}>Request Payload Type</Typography>
                <TextField
                    fullWidth
                    size="small"
                    value={adapterConfig.requestPayloadType}
                    onChange={(e) => setAdapterConfig({...adapterConfig, requestPayloadType: e.target.value})}
                />
                <Typography variant="subtitle2" sx={{mt: 2}}>Supported Methods</Typography>
                <FormControl fullWidth size="small">
                    <Select
                        multiple
                        value={adapterConfig.supportedMethods}
                        onChange={(e) => setAdapterConfig({...adapterConfig, supportedMethods: [...e.target.value]})}
                        input={<OutlinedInput placeholder="Methods"/>}
                        renderValue={(selected) => selected.join(', ')}
                        variant={"filled"}
                    >
                        {['GET', 'POST', 'PUT', 'DELETE'].map((method) => (
                            <MenuItem key={method} value={method}>
                                <Checkbox checked={adapterConfig.supportedMethods.includes(method)}/>
                                <ListItemText primary={method}/>
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </Paper>
            <Box
                sx={{
                    position: 'fixed',
                    bottom: 16,
                    mr: 38,
                    right: 16,
                    zIndex: 1000
                }}
                onMouseEnter={() => setMenuOpen(true)}
                onMouseLeave={() => setMenuOpen(false)}
            >
                <Box
                    sx={{
                        backgroundColor: 'background.paper',
                        border: '1px solid',
                        borderColor: 'divider',
                        borderRadius: '24px',
                        width: 48,
                        height: menuOpen ? 'auto' : 48,
                        overflow: 'hidden',
                        transition: 'height 0.3s ease'
                    }}
                >
                    <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', py: 1}}>
                        {menuOpen ? (
                            <>
                                <IconButton size="small" aria-label="Валидировать структуру">
                                    <ValidateIcon/>
                                </IconButton>
                                <IconButton size="small" aria-label="Сохранить маршрут">
                                    <SaveIcon/>
                                </IconButton>
                                <Divider sx={{width: '80%', my: 1}}/>
                                <IconButton size="small" aria-label="Собрать образ">
                                    <BuildIcon/>
                                </IconButton>
                                <IconButton size="small" aria-label="Запустить контейнер">
                                    <StartIcon/>
                                </IconButton>
                                <IconButton size="small" aria-label="Остановить контейнер">
                                    <StopIcon/>
                                </IconButton>
                                <IconButton size="small" aria-label="Перезапустить контейнер">
                                    <RestartIcon/>
                                </IconButton>
                                <IconButton size="small" aria-label="Удалить контейнер">
                                    <DeleteIcon/>
                                </IconButton>
                            </>
                        ) : (
                            <IconButton size="small" aria-label="Центр управления"
                                        sx={{flex: "auto", justifyContent: "center", alignItems: "center"}}>
                                <BoltIcon name="controlCenterClosedIcon" sx={{ color: "accent.main" }}/>
                            </IconButton>
                        )}
                    </Box>
                </Box>
            </Box>
        </Box>
    )
}