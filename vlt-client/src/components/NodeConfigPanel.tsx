import React, {useEffect, useMemo, useState} from "react";
import {useQuery} from "@tanstack/react-query";
import {JsonForms} from "@jsonforms/react";
import {
    materialRenderers,
    materialCells,
} from "@jsonforms/material-renderers";
import {Node} from "@xyflow/react";
import {Box, Theme, Typography, useTheme} from "@mui/material";
import {AdaptersApiService} from "@/api/sdk.gen.ts";
import {AdapterDto} from "@/api";
import {UISchemaElement} from "@jsonforms/core";

interface NodeConfigPanelProps {
    node: Node;
    setNodes: (updater: (nodes: Node[]) => Node[]) => void;
}

export const NodeConfigPanel: React.FC<NodeConfigPanelProps> = ({node, setNodes}) => {
    const theme: Theme = useTheme();

    const adapter = (node.data.adapter as AdapterDto);
    const [configData, setConfigData] = useState<any>(() => node.data.config ?? {});

    const {
        data: schemaData
    } = useQuery({
        queryKey: ["adapterConfigSchema", adapter.id],
        queryFn: () =>
            AdaptersApiService.getAdapterConfigSchema({path: {id: adapter.id}})
                .then(r => r.data),
        enabled: !!adapter.id
    });

    const schema: Record<string, any> = useMemo(() => {
        if (!schemaData) return null;
        return typeof schemaData === "string" ? JSON.parse(schemaData) : schemaData;
    }, [schemaData]);

    useEffect(() => {
        setConfigData(node.data.config ?? {});
    }, [node.id]);

    useEffect(() => {
        setNodes((nodes: Node[]) =>
            nodes.map((n: Node) => n.id === node.id ? {...n, data: {...n.data, config: configData},} : n)
        );
    }, [configData, setNodes, node.id]);

    if (!schema) {
        return <Typography>Загрузка схемы…</Typography>;
    }

    const uiSchema: UISchemaElement = {
        type: "VerticalLayout",
        options: {
            spacing: 2,
        },
        elements: Object.keys(schema.properties!).map(key => ({
            type: "Control",
            scope: `#/properties/${key}`,
        })),
    };

    return (
        <Box sx={{mt: 1}}>
            <Typography variant="h4"
                        color={theme.palette.accent.main}
                        gutterBottom
                        sx={{mb: 2}}
            >
                Узел "{adapter.displayName}"
            </Typography>
            <JsonForms
                schema={schema}
                uischema={uiSchema}
                data={configData}
                renderers={materialRenderers}
                cells={materialCells}
                onChange={({data}) => setConfigData(data)}
            />
        </Box>
    );
};