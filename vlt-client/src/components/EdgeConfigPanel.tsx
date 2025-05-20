import React from "react";
import {
    MarkerType,
    Edge as FlowEdge
} from "@xyflow/react";
import {
    Box,
    TextField,
    FormControlLabel,
    Checkbox,
    MenuItem,
    Typography
} from "@mui/material";

interface EdgeConfigPanelProps {
    edgeId: string;
    edges: FlowEdge[];
    setEdges: (updater: (edges: FlowEdge[]) => FlowEdge[]) => void;
}

export const EdgeConfigPanel: React.FC<EdgeConfigPanelProps> = ({edgeId, edges, setEdges}) => {
    const edge = edges.find(e => e.id === edgeId)!;

    const update = (patch: Partial<FlowEdge>) =>
        setEdges(es => es.map(e => (e.id === edgeId ? {...e, ...patch} : e)));

    const startType: string =
        typeof edge.markerStart === "string"
            ? edge.markerStart
            : edge.markerStart?.type ?? "";

    const endType: string =
        typeof edge.markerEnd === "string"
            ? edge.markerEnd
            : edge.markerEnd?.type ?? "";

    const markerOptions = Object.entries(MarkerType) as Array<[keyof typeof MarkerType, string]>;

    return (
        <Box sx={{display: "flex", flexDirection: "column", gap: 2, p: 1}}>
            <Typography variant="h6">Соединение #{edge.id}</Typography>

            <TextField
                label="Узел-источник"
                value={edge.source}
                disabled
            />
            <TextField
                label="Узел-потребитель"
                value={edge.target}
                disabled
            />

            <TextField
                select
                label="Тип соединения"
                value={edge.type}
                onChange={e => update({type: e.target.value})}
            >
                {["default", "straight", "step", "smoothstep"].map(t => (
                    <MenuItem key={t} value={t}>{t}</MenuItem>
                ))}
            </TextField>

            <TextField
                select
                label="Маркер начала соединения"
                value={startType}
                onChange={e => {
                    const val = e.target.value;
                    if (val === "default") {
                        update({ markerStart: undefined });
                    } else {
                        update({ markerStart: { type: val as MarkerType } });
                    }
                }}
            >
                <MenuItem key="default" value="default">
                    Default
                </MenuItem>
                {markerOptions.map(([label, val]) => (
                    <MenuItem key={val} value={val}>
                        {label}
                    </MenuItem>
                ))}
            </TextField>

            <TextField
                select
                label="Маркер конца соединения"
                value={endType}
                onChange={e => {
                    const val = e.target.value;
                    if (val === "default") { // TODO: Make correct parsing from undefined markerEnd to 'default' value in DTO
                        update({markerEnd: undefined});
                    } else {
                        update({markerEnd: {type: val as MarkerType}});
                    }
                }}
            >
                <MenuItem key="default" value="default">
                    Default
                </MenuItem>
                {markerOptions.map(([label, val]) => (
                    <MenuItem key={val} value={val}>
                        {label}
                    </MenuItem>
                ))}
            </TextField>

            <FormControlLabel
                control={
                    <Checkbox
                        checked={edge.animated ?? false}
                        onChange={(_, checked) => update({animated: checked})}
                    />
                }
                label="Анимация включена"
            />
        </Box>
    );
};