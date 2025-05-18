import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Box} from "@mui/material";
import {
    CreateRouteRequestDto,
    DockerApiService,
    DockerNetworkDto,
    RoutesApiService
} from "@/api";
import {RouteForm} from "@/components/RouteForm.tsx";
import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {NetworkDialog} from "@/components/CreateNetworkDialog.tsx";

export const CreateRoutePage: React.FC = () => {
    const navigate = useNavigate();

    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();

    const [networks, setNetworks] = useState<DockerNetworkDto[]>([]);
    const [netDialogOpen, setNetDialogOpen] = useState(false);

    useEffect(() => {
        DockerApiService.getDockerNetworks()
            .then((res) => setNetworks(res.data))
            .catch(console.error);
    }, []);

    const initialVals = React.useMemo(
        () => ({} as Partial<CreateRouteRequestDto & { id?: string }>),
        []
    );

    const handleCreateNetwork = async (name: string, driver: string) => {
        await DockerApiService.createDockerNetwork({body: {name, driver}});
        const updated = await DockerApiService.getDockerNetworks();
        setNetworks(updated.data);
    };

    const handleCreateRoute = async (dto: CreateRouteRequestDto) => {
        await RoutesApiService.createRoute({body: dto});
        navigate("/routes");

    };

    return (
        <>
            <Header currPath={["Маршруты", "Создание"]}/>
            <Sidebar/>

            <Box sx={{
                flexGrow: 1,
                p: 3,
                ml: `${sidebarWidth}px`,
                transition: "margin .2s"
            }}>
                <RouteForm
                    networks={networks}
                    onCreateNetworkClick={() => setNetDialogOpen(true)}
                    initialValues={initialVals}
                    submitLabel="Создать"
                    onSubmit={handleCreateRoute}
                />

                <NetworkDialog
                    open={netDialogOpen}
                    onClose={() => setNetDialogOpen(false)}
                    onCreate={handleCreateNetwork}
                    defaultDriver="bridge"
                />
            </Box>
        </>
    );
}