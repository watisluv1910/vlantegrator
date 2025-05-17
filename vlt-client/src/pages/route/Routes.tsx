import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import {
    Box,
    TextField,
    IconButton,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    CircularProgress,
    Alert,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Button,
    Typography,
    Tooltip, MenuItem,
    Select,
    FormControl,
    InputLabel,
    useTheme
} from "@mui/material";
import {
    Add as AddIcon,
    Edit as EditIcon,
    DeleteForever as DeleteIcon,
} from "@mui/icons-material";
import {Options, RoutesApiService} from "@/api/sdk.gen.ts";
import {type DeleteRouteData, Field, RouteDto} from "@/api/types.gen.ts";
import {keepPreviousData, useQuery} from "@tanstack/react-query";
import {useDebounce} from "@/hooks/useDebounce.ts";
import {ROUTES_SEARCH_DEBOUNCE_MS} from "@/utils/constants.tsx";
import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";

export const RoutesPage: React.FC = () => {
    const navigate = useNavigate();

    const theme = useTheme();

    const [search, setSearch] = useState("");
    const [searchField, setSearchField] = useState<Field>("NAME");

    const debouncedSearch = useDebounce(search.trim(), ROUTES_SEARCH_DEBOUNCE_MS);
    const debouncedField = useDebounce(searchField, ROUTES_SEARCH_DEBOUNCE_MS);

    const {data, isLoading, isError, refetch} = useQuery<RouteDto[], Error>({
        queryKey: ["routes", debouncedField, debouncedSearch],
        queryFn: async () => {
            if (debouncedSearch.trim() === "") {
                const res = await RoutesApiService.getAllRoutes();
                return res.data;
            }
            const searchRequest = {body: {field: debouncedField, query: debouncedSearch}};
            const res = await RoutesApiService.searchRoutes(searchRequest);
            return res.data;
        },
        placeholderData: keepPreviousData,
    });

    const routes: RouteDto[] = data ?? [];

    const [toDelete, setToDelete] = useState<string | null>(null);
    const [deleting, setDeleting] = useState(false);

    const handleDeleteConfirm = async () => {
        if (!toDelete) return;
        setDeleting(true);
        try {
            const opts: Options<DeleteRouteData> = {path: {id: toDelete}};
            await RoutesApiService.deleteRoute(opts);
            await refetch();
            setToDelete(null);
        } catch {
            alert("Ошибка при удалении маршрута: " + toDelete);
        } finally {
            setDeleting(false);
        }
    };

    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();

    return (
        <>
            <Header currPath={["Маршруты"]}/>
            <Sidebar/>

            <Box sx={{
                flexGrow: 1,
                p: 3,
                ml: `${sidebarWidth}px`,
                transition: "margin .2s"
            }}>
                <Box mb={2} display="flex" alignItems="center" justifyContent="space-between">
                    <Typography variant="h5">Маршруты</Typography>
                    <Box display="flex" alignItems="center" gap={1}>
                        <FormControl size="small">
                            <InputLabel>Критерий</InputLabel>
                            <Select
                                label="Критерий"
                                value={searchField}
                                onChange={e => setSearchField(e.target.value)}
                            >
                                <MenuItem value="ID">Идентификатор</MenuItem>
                                <MenuItem value="NAME">Название</MenuItem>
                                <MenuItem value="OWNER">Владелец</MenuItem>
                            </Select>
                        </FormControl>
                        <TextField
                            size="small"
                            placeholder="Поиск…"
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                        />
                        <Tooltip title="Создать новый маршрут">
                            <IconButton color="primary" onClick={() => navigate("/routes/create")}>
                                <AddIcon/>
                            </IconButton>
                        </Tooltip>
                    </Box>
                </Box>

                {isError && <Alert severity="error" sx={{mb: 2}}>Не удалось загрузить маршруты</Alert>}

                {isLoading ? (
                    <Box textAlign="center" mt={4}><CircularProgress/></Box>
                ) : routes.length === 0 ? (
                    <Box
                        sx={{
                            height: "60vh",
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center"
                        }}
                    >
                        <Typography variant="h2" color="text.secondary">
                            Маршруты не найдены
                        </Typography>
                    </Box>
                ) : (
                    <TableContainer component={Paper}>
                        <Table stickyHeader size="small" aria-label="Routes Table">
                            <TableHead>
                                <TableRow>
                                    <TableCell
                                        variant="head"
                                        style={{backgroundColor: theme.palette.accent.main}}
                                    >
                                        Название
                                    </TableCell>
                                    <TableCell
                                        variant="head"
                                        style={{backgroundColor: theme.palette.accent.main}}
                                        sx={{
                                            borderLeft: theme => `1px solid ${theme.palette.divider}`,
                                        }}
                                    >
                                        Владелец
                                    </TableCell>
                                    <TableCell
                                        variant="head"
                                        style={{backgroundColor: theme.palette.accent.main}}
                                        sx={{
                                            borderLeft: theme => `1px solid ${theme.palette.divider}`,
                                        }}
                                    >
                                        Описание
                                    </TableCell>
                                    <TableCell
                                        align="center"
                                        variant="head"
                                        style={{backgroundColor: theme.palette.accent.main}}
                                        sx={{
                                            borderLeft: theme => `1px solid ${theme.palette.divider}`,
                                            width: 120
                                        }}
                                    >
                                        Действия
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {routes.map(rt => (
                                    <TableRow key={rt.routeId.id} hover>
                                        <TableCell
                                            variant="body"
                                            sx={{cursor: "pointer"}}
                                            onClick={() => navigate(`/routes/${rt.routeId.id}/definition`)}
                                        >
                                            {rt.name}
                                        </TableCell>
                                        <TableCell
                                            variant="body"
                                            sx={{
                                                borderLeft: theme => `1px solid ${theme.palette.divider}`
                                            }}
                                        >
                                            {rt.ownerName}
                                        </TableCell>
                                        <TableCell
                                            variant="body"
                                            sx={{
                                                maxWidth: 200,
                                                whiteSpace: 'nowrap',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                borderLeft: theme => `1px solid ${theme.palette.divider}`
                                            }}
                                        >
                                            {rt.description}
                                        </TableCell>
                                        <TableCell
                                            variant="body"
                                            align="center"
                                            sx={{
                                                display: "flex",
                                                justifyContent: "space-evenly",
                                                borderLeft: theme => `1px solid ${theme.palette.divider}`,
                                                width: 120
                                            }}
                                        >
                                            <Tooltip title="Редактировать">
                                                <IconButton size="small"
                                                            onClick={() => navigate(`/routes/${rt.routeId.id}/edit`)}
                                                            sx={{border: theme => `1px solid ${theme.palette.divider}`}}
                                                >
                                                    <EditIcon fontSize="small"/>
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title="Удалить">
                                                <IconButton
                                                    size="small"
                                                    color="error"
                                                    onClick={() => setToDelete(rt.routeId.id)}
                                                    sx={{border: theme => `1px solid ${theme.palette.divider}`}}
                                                >
                                                    <DeleteIcon fontSize="small"/>
                                                </IconButton>
                                            </Tooltip>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                <Dialog open={!!toDelete} onClose={() => setToDelete(null)}>
                    <DialogTitle>Удаление маршрута</DialogTitle>
                    <DialogContent>
                        <DialogContentText>Действие необратимо. Продолжить?</DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setToDelete(null)} disabled={deleting}>Отмена</Button>
                        <Button color="error" onClick={handleDeleteConfirm} disabled={deleting}>
                            {deleting ? "Удаление…" : "Удалить"}
                        </Button>
                    </DialogActions>
                </Dialog>
            </Box>
        </>
    );
};