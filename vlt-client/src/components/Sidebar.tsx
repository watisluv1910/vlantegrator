import {useAuth} from "react-oidc-context";
import {useNavigate} from "react-router-dom";
import {MAIN_ROUTES, Route} from "../utils/constants.js";
import {useColorScheme} from "@mui/material/styles";
import {
    Box,
    Button,
    Divider,
    Drawer,
    IconButton,
    List,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Toolbar,
    Typography,
} from "@mui/material";
import {
    ChevronLeft as ChevronLeftIcon,
    Menu as MenuIcon,
    LightMode as LightModeIcon,
    DarkMode as DarkModeIcon,
} from "@mui/icons-material";
import {
    MAIN_SIDEBAR_COLLAPSED_WIDTH,
    MAIN_SIDEBAR_OPENED_WIDTH,
    MainSidebarContext
} from "../hooks/sidebarContexts.tsx";

type NavItem = {
    route: Route,
    action: () => void
}

export const Sidebar = () => {
    const auth = useAuth();
    const navigate = useNavigate();

    const navItems: NavItem[] = [];

    for (let name in MAIN_ROUTES) {
        let route: Route = MAIN_ROUTES[name];
        if (auth.isAuthenticated || !route?.protected) {
            navItems.push({
                route: route,
                action: () => navigate(route?.path)
            });
        }
    }

    const [sidebarWidth, setSidebarWidth] = MainSidebarContext.useSidebarWidth();
    const [sidebarOpen, setSidebarOpen] = MainSidebarContext.useSidebarOpen();

    const toggleDrawer = () => setSidebarOpen(prev => {
        setSidebarWidth(!prev ? MAIN_SIDEBAR_OPENED_WIDTH : MAIN_SIDEBAR_COLLAPSED_WIDTH)
        return !prev;
    });

    const {mode, setMode} = useColorScheme();
    const toggleTheme = () => setMode(mode === "dark" ? "light" : "dark"); // TODO: Rework

    return (
        <>
            <Drawer
                variant="permanent"
                open={sidebarOpen}
                sx={{
                    width: sidebarWidth,
                    flexShrink: 0,
                    "& .MuiDrawer-paper": {
                        width: sidebarWidth,
                        boxSizing: "border-box",
                        overflowX: "hidden",
                        transition: "width .2s"
                    }
                }}
            >
                <Toolbar sx={{justifyContent: sidebarOpen ? "space-between" : "center"}}>
                    {sidebarOpen && (
                        <Typography fontFamily="Madimi One, sans-serif" variant="h3" noWrap sx={{cursor: "default"}}>
                            Vlantegrator
                        </Typography>
                    )}
                    <IconButton color="inherit" onClick={toggleDrawer}>
                        {sidebarOpen ? <ChevronLeftIcon/> : <MenuIcon/>}
                    </IconButton>
                </Toolbar>
                <Divider/>
                <List>
                    {navItems.map(({route, action}) => (
                        <ListItemButton
                            key={route.text}
                            sx={{
                                justifyContent: sidebarOpen ? "initial" : "center",
                                px: 2.5,
                                height: "50px",
                            }}
                            onClick={action}
                        >
                            <ListItemIcon
                                sx={{
                                    minWidth: 0,
                                    mr: sidebarOpen ? 3 : "auto",
                                    justifyContent: "center"
                                }}
                            >
                                {route.icon}
                            </ListItemIcon>
                            {sidebarOpen && <ListItemText primary={route.text}/>}
                        </ListItemButton>
                    ))}
                </List>
                <Divider/>
                <Box sx={{position: "absolute", bottom: 0, width: "100%", textAlign: "center", py: 1}}>
                    <Button onClick={toggleTheme}
                            color="inherit"
                            sx={{gap: 1, borderRadius: 2}}
                    >
                        {mode === "dark" ? <LightModeIcon/> : <DarkModeIcon/>}
                        {sidebarOpen && (
                            <>
                                <Divider orientation="vertical" flexItem sx={{mx: 1, borderColor: "inherit"}}/>
                                <Typography>{mode === "dark" ? "Светлый режим" : "Тёмный режим"}</Typography>
                            </>
                        )}
                    </Button>
                </Box>
            </Drawer>
        </>
    )
}