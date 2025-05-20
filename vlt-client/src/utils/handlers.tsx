import React from "react";
import {Node, ReactFlowInstance} from "@xyflow/react";
import {Tooltip} from "@mui/material";
import {AdapterDto} from "@/api";
import {adapterIconMap} from "@/utils/adapterUtils.ts";
import {FLOW_NODE_DEFAULT_SIZE, FLOW_NODE_DEFAULTS} from "@/utils/constants.tsx";

export const handleCopyEvent = (e: React.MouseEvent<HTMLElement>, content: any) => {
    e.stopPropagation();
    navigator.clipboard
        .writeText(content)
        .then()
        .catch(err => {
            console.error("Failed to write into clipboard:", err);
        });
};

export const handleAddNode = (
    flow: ReactFlowInstance,
    adapter: AdapterDto,
    setNodes: React.Dispatch<React.SetStateAction<Node[]>>
) => {
    const {x: panX, y: panY, zoom} = flow.getViewport();
    const centerX: number = window.innerWidth / 2;
    const centerY: number = window.innerHeight / 2;

    const graphX: number = (centerX - panX) / zoom;
    const graphY: number = (centerY - panY) / zoom;

    const Icon = adapterIconMap[adapter.type];

    const newNode: Node = {
        id: crypto.randomUUID(),
        type: "default",
        data: {
            config: {},
            adapter: adapter,
            label: (
                <Tooltip key={adapter.id} title={adapter.displayName}>
                    <Icon sx={{width: "100%", height: "100%"}}/>
                </Tooltip>
            ),
        },
        position: {x: graphX, y: graphY},
        style: {
            width: FLOW_NODE_DEFAULT_SIZE,
            height: FLOW_NODE_DEFAULT_SIZE,
            borderRadius: `50%`,
        },
        ...FLOW_NODE_DEFAULTS
    };

    setNodes((nodes: Node[]): Node[] => [...nodes, newNode]);
};