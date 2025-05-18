// src/pages/EditRoutePage.tsx
import React, {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {Box, CircularProgress, Alert} from "@mui/material";
import {useQuery, useMutation, useQueryClient} from "@tanstack/react-query";

import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {RouteForm} from "@/components/RouteForm.tsx";
import {NetworkDialog} from "@/components/CreateNetworkDialog.tsx";

import {
    DockerApiService,
    RoutesApiService,
} from "@/api/sdk.gen";
import type {
    RouteDto,
    DockerNetworkDto,
    UpdateRouteRequestDto,
} from "@/api/types.gen";

export const EditRoutePage: React.FC = () => {
    const {id} = useParams<{ id: string }>();
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const {data: route, isLoading: routeLoading, isError: routeError} = useQuery<RouteDto, Error>({
        queryKey: ["route", id],
        queryFn: async () => {
            const resp = await RoutesApiService.getRoute({path: {id: id!}});
            return resp.data;
        },
        enabled: !!id,
    });

    const {data: networks, isLoading: netsLoading, isError: netsError,} = useQuery<DockerNetworkDto[], Error>({
        queryKey: ["dockerNetworks"],
        queryFn: async () => {
            const resp = await DockerApiService.getDockerNetworks();
            return resp.data;
        },
        staleTime: 60_000
    });

    const updateRoute = useMutation({
        mutationFn: (dto: UpdateRouteRequestDto) =>
            RoutesApiService.updateRoute({path: {id: id!}, body: dto}),
        async onSuccess() {
            await queryClient.invalidateQueries({queryKey: ["route", id]});
            navigate("/routes");
        },
    });

    const [dialogOpen, setDialogOpen] = useState(false);

    const handleCreateNetwork = async (name: string, driver: string) => {
        await DockerApiService.createDockerNetwork({body: {name, driver}});
        await queryClient.invalidateQueries({queryKey: ["dockerNetworks"]});
    };

    if (routeLoading || netsLoading) {
        return (
            <Box textAlign="center" mt={8}>
                <CircularProgress/>
            </Box>
        );
    }
    if (routeError) {
        return <Alert severity="error">Не удалось загрузить маршрут</Alert>;
    }
    if (netsError) {
        return <Alert severity="error">Не удалось загрузить сети Docker</Alert>;
    }

    const initialValues = {
        id: route?.routeId.id,
        name: route?.name,
        description: route?.description,
        ownerName: route?.ownerName,
        publishedPorts: route?.publishedPorts,
        networks: route?.networks,
        env: route?.env,
    };

    return (
        <>
            <Header currPath={["Маршруты", `${route?.name}`, "Редактирование"]}/>
            <Sidebar/>

            <Box
                sx={{
                    flexGrow: 1,
                    p: 3,
                    ml: `${MainSidebarContext.useSidebarWidth()[0]}px`,
                    transition: "margin .2s",
                }}
            >
                <RouteForm
                    networks={networks!}
                    onCreateNetworkClick={() => setDialogOpen(true)}
                    initialValues={initialValues}
                    showIdField={true}
                    submitLabel="Обновить"
                    onSubmit={async (dto: UpdateRouteRequestDto) => {
                        await updateRoute.mutateAsync(dto);
                    }}
                />

                <NetworkDialog
                    open={dialogOpen}
                    onClose={() => setDialogOpen(false)}
                    onCreate={async (name, driver) => await handleCreateNetwork(name, driver)}
                    defaultDriver="bridge"
                />
            </Box>
        </>
    );
};