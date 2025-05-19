import React, {useCallback, useEffect, useState} from "react";
import {
    addEdge,
    Background,
    Controls, Edge,
    MarkerType,
    Position,
    ReactFlow,
    ReactFlowProvider,
    Node,
    Connection,
    useEdgesState,
    useNodesState,
    useReactFlow,
    useOnSelectionChange, OnSelectionChangeParams
} from "@xyflow/react";
import {
    Box,
    Checkbox, Divider,
    FormControl,
    IconButton,
    InputLabel,
    ListItemText,
    MenuItem,
    OutlinedInput, Paper,
    Select,
    SelectChangeEvent,
    TextField, Tooltip,
    Typography, useTheme
} from "@mui/material";
import {
    Storage as JdbcAdapterIcon,
    Http as HttpAdapterIcon,
    Transform as TransformerAdapterIcon,
    HistoryEdu as LoggerAdapterIcon,
} from "@mui/icons-material";
import {useColorScheme} from "@mui/material/styles";
import {AdaptersApiService} from "@/api/sdk.gen.ts";
import {AdapterDto} from "@/api/types.gen.ts";
import {adapterIconMap} from "@/utils/adapterUtils.ts";
import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {IntegratorSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {IntegratorSidebar} from "@/components/IntegratorSidebar.tsx";
import {IntegratorPowerTool} from "@/components/IntegratorPowerTool.tsx";

import "@xyflow/react/dist/style.css";

type AdaptersGroupBy = "type" | "direction" | "channelKind";

const nodeDefaults = {
    sourcePosition: Position.Bottom,
    targetPosition: Position.Top,
};

const initialNodes: Node[] = [
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

const ADAPTER_PREVIEW_SIZE = 48;

export const IntegratorPage: React.FC = () => {
    return (
        <IntegratorSidebarContext.SidebarProvider initialOpen>
            <ReactFlowProvider>
                <Integrator/>
            </ReactFlowProvider>
        </IntegratorSidebarContext.SidebarProvider>
    )
}

const Integrator: React.FC = () => {
    const theme = useTheme();
    const {mode} = useColorScheme();
    const [sidebarWidth] = IntegratorSidebarContext.useSidebarWidth();

    const reactFlow = useReactFlow();
    const [nodes, setNodes, onNodesChange] = useNodesState<Node>(initialNodes);
    const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>(initialEdges);

    const [selection, setSelection] = useState<Array<Node | Edge>>([]);

    const onSelectionChange = useCallback(
        (elements: OnSelectionChangeParams) => setSelection([...elements.edges, ...elements.nodes]),
        []
    );

    useOnSelectionChange({onChange: onSelectionChange});

    const [adapters, setAdapters] = useState<AdapterDto[]>([]);
    const [groupBy, setGroupBy] = useState<AdaptersGroupBy>("type");

    useEffect(() => {
        AdaptersApiService.getAllAdapters()
            .then(r => setAdapters(r.data))
            .catch(console.error);
    }, []);

    const buckets = adapters.reduce<Record<string, AdapterDto[]>>((acc, a) => {
        const key = (a as any)[groupBy] as string;
        (acc[key] ??= []).push(a);
        return acc;
    }, {});

    const handleAddAdapter = (a: AdapterDto) => {
        const {x: panX, y: panY, zoom} = reactFlow.getViewport();
        const centerX = window.innerWidth / 2;
        const centerY = window.innerHeight / 2;

        const graphX = (centerX - panX) / zoom;
        const graphY = (centerY - panY) / zoom;

        const Icon = adapterIconMap[a.type];

        const newNode: Node = {
            id: crypto.randomUUID(),
            type: "default",
            data: {
                label: (
                    <Box sx={{textAlign: "center"}}>
                        <Icon fontSize="small"/>
                        <Typography variant="caption">{a.displayName}</Typography>
                    </Box>
                ),
            },
            position: {x: graphX, y: graphY},
            style: {borderRadius: 16, width: 120, height: 60},
            sourcePosition: Position.Bottom,
            targetPosition: Position.Top,
        };

        setNodes(nds => [...nds, newNode]);
    };

    const onConnect = useCallback(
        (params: Edge | Connection) => setEdges(es => addEdge(params, es)),
        [],
    );

    // TODO: Delete
    const [adapterConfig, setAdapterConfig] = useState({
        path: "/data",
        requestPayloadType: "java.lang.String",
        supportedMethods: ["POST"]
    });

    return (
        <>
            <Header currPath={["Маршруты", "Редактор"]}/>
            <Sidebar/>

            <Box sx={{height: "92vh", width: `calc(100% - ${sidebarWidth}px)`, transition: "margin .2s"}}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    fitView
                    elementsSelectable
                    elevateEdgesOnSelect
                    elevateNodesOnSelect
                    multiSelectionKeyCode="none"
                    colorMode={mode}
                    proOptions={{hideAttribution: true}}
                    style={{width: "100%", height: "100%", zIndex: -1}}
                >
                    <Controls
                        position="center-right"
                        orientation="vertical"
                    />
                    <Background/>
                </ReactFlow>
            </Box>

            <IntegratorSidebar>
                {selection.length === 0 ? (
                    <Box>
                        <Typography
                            variant="h4"
                            color={theme.palette.accent.main}
                            gutterBottom
                            sx={{mt: 1}}
                        >
                            Адаптеры
                        </Typography>

                        <FormControl fullWidth size="small" sx={{my: 2}}>
                            <InputLabel>Группировать по критерию</InputLabel>
                            <Select
                                label="Группировать по критерию"
                                value={groupBy}
                                onChange={(e: SelectChangeEvent) => setGroupBy(e.target.value as AdaptersGroupBy)}
                            >
                                <MenuItem value="type">Тип</MenuItem>
                                <MenuItem value="direction">Направление</MenuItem>
                                <MenuItem value="channelKind">Вид канала</MenuItem>
                            </Select>
                        </FormControl>

                        {Object.entries(buckets).map(([bucket, list]: [string, AdapterDto[]]): React.JSX.Element => (
                            <>
                                <Paper key={bucket}
                                       elevation={1}
                                       sx={{
                                           p: 2,
                                           mb: 2,
                                           borderRadius: 1,
                                       }}
                                >
                                    <Typography variant="subtitle1">{bucket}</Typography>
                                    <Divider sx={{my: 1}} variant="fullWidth"/>
                                    <Box sx={{display: "flex", flexWrap: "wrap", gap: 1}}>
                                        {list.map(a => {
                                            let AdapterIcon = adapterIconMap[a.type];
                                            return (
                                                <Tooltip key={a.id} title={a.displayName}>
                                                    <IconButton
                                                        onClick={() => handleAddAdapter(a)}
                                                        // @ts-ignore
                                                        color="accent"
                                                        sx={{
                                                            width: ADAPTER_PREVIEW_SIZE,
                                                            aspectRatio: "1/1",
                                                            border: theme => `1px solid ${theme.palette.divider}`,
                                                            backgroundColor: `${theme.palette.primary.light}`,
                                                        }}
                                                    >
                                                        <AdapterIcon/>
                                                    </IconButton>
                                                </Tooltip>
                                            );
                                        })}
                                    </Box>
                                </Paper>
                            </>
                        ))}
                    </Box>
                ) : (
                    <>
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
                    </>
                )}
            </IntegratorSidebar>

            <IntegratorPowerTool offsetRight={sidebarWidth}/>
        </>
    );
};