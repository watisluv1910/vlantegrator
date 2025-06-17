import React, {useCallback, useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {
    addEdge,
    Background,
    BackgroundVariant,
    Connection,
    Controls,
    Edge,
    MarkerType,
    Node,
    OnSelectionChangeParams,
    ReactFlow,
    ReactFlowProvider,
    useEdgesState,
    useNodesState,
    useOnSelectionChange,
    useReactFlow
} from "@xyflow/react";
import {useQuery} from "@tanstack/react-query";
import {
    Alert,
    Box,
    Divider,
    FormControl,
    IconButton,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    SelectChangeEvent,
    Theme,
    Tooltip,
    Typography,
    useTheme
} from "@mui/material";
import {useColorScheme} from "@mui/material/styles";
import {AdaptersApiService, RoutesApiService} from "@/api/sdk.gen.ts";
import {AdapterDto, ConnectionDto, NodeDto, RouteDefinitionDto, RouteDto} from "@/api/types.gen.ts";
import {adapterIconMap} from "@/utils/adapterUtils.ts";
import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {IntegratorSidebarContext, MainSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {IntegratorSidebar} from "@/components/IntegratorSidebar.tsx";
import {IntegratorPowerTool} from "@/components/IntegratorPowerTool.tsx";

import "@xyflow/react/dist/style.css";
import {ADAPTER_PREVIEW_SIZE, FLOW_NODE_DEFAULTS, SNAP_SIZE} from "@/utils/constants.tsx";
import {handleAddNode} from "@/utils/handlers.tsx";
import {EdgeConfigPanel} from "@/components/EdgeConfigPanel.tsx";
import {NodeConfigPanel} from "@/components/NodeConfigPanel.tsx";

type AdaptersGroupBy = "type" | "direction" | "channelKind";

export const IntegratorPage: React.FC = () => {
    const {id} = useParams<{ id: string }>();

    if (!id) return (
        <Alert severity="error">
            <Typography>Ошибка при получении данных маршрута. Проверьте корректность URL</Typography>
        </Alert>
    );

    return (
        <IntegratorSidebarContext.SidebarProvider initialOpen>
            <ReactFlowProvider>
                <Integrator routeId={id}/>
            </ReactFlowProvider>
        </IntegratorSidebarContext.SidebarProvider>
    )
};

interface IntegratorProps {
    routeId: string,
}

const extractNodes = (
    routeDefinition: RouteDefinitionDto,
    adapters: AdapterDto[]
): Node[] => {
    return routeDefinition.nodes.map((node: NodeDto): Node | null => {
        let adapter = adapters.find(a => a.id == node.adapterId);

        if (adapter === undefined) {
            console.error(`Adapter with id: ${node.adapterId} not found for node: ${JSON.stringify(node)}`);
            return null;
        }

        let Icon = adapterIconMap[adapter.type];
        return ({
            id: node.id,
            type: node.style.type,
            data: {
                config: node.config,
                adapter: adapter,
                label: (
                    <Tooltip key={adapter.id} title={adapter.displayName}>
                        <Icon sx={{width: "100%", height: "100%"}}/>
                    </Tooltip>
                ),
            },
            position: {x: node.position.x, y: node.position.y},
            style: {
                ...node.style.config,
            },
            ...FLOW_NODE_DEFAULTS
        });
    }).filter(it => it != null);
};

const extractEdges = (routeDefinition: RouteDefinitionDto): Edge[] => {
    return routeDefinition.connections.map((connection: ConnectionDto): Edge => ({
        id: connection.id,
        source: connection.sourceId,
        target: connection.targetId,
        type: connection.style.type,
        markerStart: {type: MarkerType[connection.style.startMarkerType as keyof typeof MarkerType]},
        markerEnd: {type: MarkerType[connection.style.endMarkerType as keyof typeof MarkerType]},
        focusable: connection.style.focusable,
        animated: connection.style.animated,
    }));
};

const Integrator: React.FC<IntegratorProps> = ({routeId}: IntegratorProps) => {
    const theme: Theme = useTheme();
    const {mode} = useColorScheme();
    const [mainSidebarWidth] = MainSidebarContext.useSidebarWidth();
    const [integratorSidebarWidth] = IntegratorSidebarContext.useSidebarWidth();

    const reactFlow = useReactFlow();

    const [isShiftPressed, setIsShiftPressed] = useState(false);

    const {
        data: adapters,
        isLoading: isLoadingAdapters,
        isError: isErrorAdapters,
        error: errorAdapters,
    } = useQuery<AdapterDto[], Error>({
        queryKey: ["adapters"],
        queryFn: () => AdaptersApiService.getAllAdapters()
            .then(r => r.data),
    });

    const {
        data: route,
        isLoading: isLoadingRoute,
        isError: isErrorRoute,
        error: errorRoute,
    } = useQuery<RouteDto, Error>({
        queryKey: ["route", routeId],
        queryFn: (): Promise<RouteDto> => RoutesApiService.getRoute({path: {id: routeId}})
            .then(r => r.data),
        enabled: !!routeId,
    });

    const {
        data: routeStatuses,
        isLoading: isLoadingStatus,
        isError: isErrorStatus,
    } = useQuery({
        queryKey: ["routeStatus", routeId],
        queryFn: () => RoutesApiService.getRoutesStatus({body: [{id: routeId, versionHash: route!.routeId.versionHash!}]})
            .then(r => r.data),
        enabled: !!routeId,
    });

    const {
        data: routeDefinition,
        isLoading: isLoadingDef,
        isError: isErrorDef,
        error: errorDef,
    } = useQuery({
        queryKey: ["routeDefinition", routeId, route?.routeId.versionHash],
        queryFn: () => RoutesApiService.getRouteDefinition({
            path: {id: routeId, versionHash: route!.routeId.versionHash!},
        }).then(r => r.data),
        enabled: Boolean(routeId) && Boolean(route?.routeId.versionHash)
    });

    const [nodes, setNodes, onNodesChange] = useNodesState<Node>([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>([]);

    useEffect(() => {
        if (adapters && routeDefinition) {
            setNodes(extractNodes(routeDefinition, adapters));
            setEdges(extractEdges(routeDefinition));
        }
    }, [adapters, routeDefinition, setNodes, setEdges]);

    const [selection, setSelection] = useState<Array<Node | Edge>>([]);

    const onSelectionChange = useCallback(
        (elements: OnSelectionChangeParams): void => setSelection([...elements.edges, ...elements.nodes]),
        []
    );

    useOnSelectionChange({onChange: onSelectionChange});

    const [groupBy, setGroupBy] = useState<AdaptersGroupBy>("type");

    const buckets = (adapters || []).reduce<Record<string, AdapterDto[]>>((acc, a) => {
        const key = (a as any)[groupBy] as string;
        (acc[key] ??= []).push(a);
        return acc;
    }, {});

    const onConnect = useCallback(
        (params: Edge | Connection) => setEdges(es => addEdge(params, es)),
        [],
    );

    useEffect(() => {
        const onKeyDown = (e: KeyboardEvent) => {
            if (e.key === 'Shift') {
                setIsShiftPressed(true);
            }
        };
        const onKeyUp = (e: KeyboardEvent) => {
            if (e.key === 'Shift') {
                setIsShiftPressed(false);
            }
        };

        window.addEventListener('keydown', onKeyDown);
        window.addEventListener('keyup', onKeyUp);
        return () => {
            window.removeEventListener('keydown', onKeyDown);
            window.removeEventListener('keyup', onKeyUp);
        };
    }, []);

    // Проверка на загрузку данных маршрута, должна происходить после инициализации всех хуков
    if (isLoadingAdapters || isLoadingRoute || isLoadingDef) {
        return <Typography>Загрузка…</Typography>;
    }
    if (isErrorAdapters) {
        return <Alert severity="error">{errorAdapters!.message}</Alert>;
    }
    if (isErrorRoute) {
        return <Alert severity="error">{errorRoute!.message}</Alert>;
    }
    if (isErrorDef) {
        return <Alert severity="error">{errorDef!.message}</Alert>;
    }

    return (
        <>
            <Header currPath={["Маршруты", route?.name ?? "…", "Редактор структуры"]}/>
            <Sidebar/>

            <Box sx={{height: "92vh", width: `calc(100% - ${integratorSidebarWidth}px)`, transition: "margin .2s"}}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    minZoom={0.2}
                    maxZoom={4}
                    fitView
                    fitViewOptions={{padding: 0.5}}
                    elementsSelectable
                    elevateEdgesOnSelect
                    elevateNodesOnSelect
                    multiSelectionKeyCode="none"
                    snapToGrid={isShiftPressed}
                    snapGrid={SNAP_SIZE}
                    colorMode={mode}
                    proOptions={{hideAttribution: true}}
                    style={{width: "100%", height: "100%", zIndex: -1}}
                >
                    <Background variant={BackgroundVariant.Dots}/> {/* TODO: Move to user settings */}
                    <Controls
                        position="center-right"
                        orientation="vertical"
                    />
                </ReactFlow>
            </Box>

            <IntegratorSidebar>
                {selection.length !== 1 ? (
                    <Box sx={{mt: 1}}>
                        <Typography
                            variant="h4"
                            color={theme.palette.accent.main}
                            gutterBottom
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

                        {Object.entries(buckets).map(([bucket, list]: [string, AdapterDto[]]) => (
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
                                    {list.map(adapter => {
                                        let AdapterIcon = adapterIconMap[adapter.type];
                                        return (
                                            <Tooltip key={adapter.id} title={adapter.displayName}>
                                                <IconButton
                                                    onClick={() => handleAddNode(reactFlow, adapter, setNodes)}
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
                        ))}
                    </Box>
                ) : (
                    (() => {
                        const sel: Node | Edge = selection[0];
                        return "source" in sel
                            ? <EdgeConfigPanel key={sel.id} edgeId={sel.id} edges={edges} setEdges={setEdges}/>
                            : <NodeConfigPanel key={sel.id} node={sel} setNodes={setNodes}/>
                    })()
                )}
            </IntegratorSidebar>

            <IntegratorPowerTool offsetRight={integratorSidebarWidth}/>

            <Box
                sx={{
                    position: 'absolute',
                    bottom: 12,
                    right: integratorSidebarWidth + 180, // чуть левее от PowerTool
                    backgroundColor: theme.palette.mode === 'dark'
                        ? theme.palette.grey[800]
                        : theme.palette.grey[200],
                    borderRadius: 2,
                    padding: '6px 12px',
                    fontSize: 14,
                    color: theme.palette.text.primary,
                    boxShadow: 1,
                }}
            >
                Статус: {isLoadingStatus ? "загрузка..." : isErrorStatus ? "ошибка" : routeStatuses?.[routeId + "." + route!.routeId.versionHash!]}
            </Box>

            {isShiftPressed && (
                <Box
                    sx={{
                        position: 'absolute',
                        bottom: 12,
                        left: `calc(16px + ${mainSidebarWidth}px)`,
                        padding: '4px 8px',
                        color: 'white',
                        opacity: 0.5,
                        background: theme.palette.common.black,
                        borderRadius: 4,
                        fontSize: 12,
                        cursor: 'default'
                    }}
                >
                    Привязка к сетке {SNAP_SIZE[0]}×{SNAP_SIZE[1]}
                </Box>
            )}
        </>
    );
};