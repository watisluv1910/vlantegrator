import {useCallback, useState} from "react";
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
    Box,
    Checkbox,
    FormControl,
    ListItemText,
    MenuItem,
    OutlinedInput,
    Select,
    TextField,
    Typography
} from "@mui/material";
import {
    Storage as JdbcAdapterIcon,
    Http as HttpAdapterIcon,
    Transform as TransformerAdapterIcon,
    HistoryEdu as LoggerAdapterIcon,
} from "@mui/icons-material";
import {useColorScheme} from "@mui/material/styles";
import {Header} from "../components/Header.tsx";
import {Sidebar} from "../components/Sidebar.tsx";
import {IntegratorSidebarContext} from "../hooks/sidebarContexts.tsx";
import {IntegratorPowerTool} from "../components/IntegratorPowerTool.tsx";
import {IntegratorSidebar} from "../components/IntegratorSidebar.tsx";

import "@xyflow/react/dist/style.css";

const nodeDefaults = {
    sourcePosition: Position.Bottom,
    targetPosition: Position.Top,
};

const initialNodes = [
    {
        id: "http-inbound",
        type: "default",
        data: {
            label: (
                <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", gap: 1}}>
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
        id: "transform",
        type: "default",
        data: {
            label: (
                <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", gap: 1}}>
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
        id: "log-http",
        type: "default",
        data: {
            label: (
                <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", gap: 1}}>
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
        id: "log-transform",
        type: "default",
        data: {
            label: (
                <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", gap: 1}}>
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
        id: "jdbc-outbound",
        type: "default",
        data: {
            label: (
                <Box sx={{display: "flex", flexDirection: "column", alignItems: "center", gap: 1}}>
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
        id: "e1",
        source: "http-inbound",
        target: "transform",
        type: "default",
        markerEnd: {type: MarkerType.ArrowClosed},
        animated: true
    },
    {id: "e2", source: "http-inbound", target: "log-http", type: "default", markerEnd: {type: MarkerType.ArrowClosed}},
    {
        id: "e3",
        source: "transform",
        target: "jdbc-outbound",
        type: "default",
        markerEnd: {type: MarkerType.ArrowClosed},
        animated: true
    },
    {id: "e4", source: "transform", target: "log-transform", type: "default", markerEnd: {type: MarkerType.ArrowClosed}}
];

export const IntegratorPage = () => {
    return (
        <IntegratorSidebarContext.SidebarProvider initialOpen={true}>
            <Integrator/>
        </IntegratorSidebarContext.SidebarProvider>
    )
}

const Integrator = () => {
    const [sidebarWidth] = IntegratorSidebarContext.useSidebarWidth();

    const [nodes, , onNodesChange] = useNodesState(initialNodes);
    const [edges, , onEdgesChange] = useEdgesState(initialEdges);

    const onConnect = useCallback(
        (params: any) => onEdgesChange(addEdge(params, edges)),
        [edges, onEdgesChange]
    );

    const {mode} = useColorScheme();

    const [adapterConfig, setAdapterConfig] = useState({
        path: "/data",
        requestPayloadType: "java.lang.String",
        supportedMethods: ["POST"]
    });

    return (
        <>
            <Header currPath={["Интегратор", "Маршруты", "Интеграция с БД теста", "Настройка потока"]}/>
            <Sidebar/>

            <Box
                sx={{
                    height: "92vh",
                    width: `calc(100%-${sidebarWidth}px)`
                }}
            >
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    attributionPosition={"top-left"}
                    nodesDraggable
                    nodesConnectable
                    elementsSelectable
                    colorMode={mode}
                    style={{width: "100%", height: "100%", zIndex: -1}}
                >
                    <Controls position={"bottom-left"}/>
                    <Background/>
                </ReactFlow>
            </Box>

            <IntegratorSidebar>
                <Typography variant="h6" gutterBottom>
                    HTTP Inbound Config
                </Typography>
                <Typography variant="subtitle2">Path</Typography>
                <TextField
                    fullWidth
                    size="small"
                    value={adapterConfig.path}
                    onChange={(e) =>
                        setAdapterConfig({...adapterConfig, path: e.target.value})
                    }
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
                        onChange={(e) => setAdapterConfig({
                            ...adapterConfig,
                            supportedMethods: [...e.target.value]
                        })}
                        input={<OutlinedInput placeholder="Methods"/>}
                        renderValue={(selected) => selected.join(", ")}
                        variant={"filled"}
                    >
                        {["GET", "POST", "PUT", "DELETE"].map((method) => (
                            <MenuItem key={method} value={method}>
                                <Checkbox checked={adapterConfig.supportedMethods.includes(method)}/>
                                <ListItemText primary={method}/>
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </IntegratorSidebar>

            <IntegratorPowerTool offsetRight={sidebarWidth}/>
        </>
    )
}